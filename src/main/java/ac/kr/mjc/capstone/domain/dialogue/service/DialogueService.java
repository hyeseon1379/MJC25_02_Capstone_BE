package ac.kr.mjc.capstone.domain.dialogue.service;

import ac.kr.mjc.capstone.domain.book.entity.Book;
import ac.kr.mjc.capstone.domain.book.repository.BookRepository;
import ac.kr.mjc.capstone.domain.dialogue.dto.*;
import ac.kr.mjc.capstone.domain.dialogue.entity.DialogueConversation;
import ac.kr.mjc.capstone.domain.dialogue.entity.EmotionType;
import ac.kr.mjc.capstone.domain.dialogue.repository.DialogueConversationRepository;
import ac.kr.mjc.capstone.domain.dialogue.repository.DialogueEmotionRepository;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogueService {

    private final DialogueConversationRepository conversationRepository;
    private final DialogueEmotionRepository emotionRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    /**
     * 대화 기록 생성
     */
    @Transactional
    public DialogueResponse createConversation(Long userId, DialogueCreateRequest request) {
        log.info("대화 기록 생성 - userId: {}", userId);

        // 사용자 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 도서 확인 (선택사항)
        Book book = null;
        if (request.getBookId() != null) {
            book = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));
        }

        // 감정 타입 검증
        validateEmotions(request.getEmotions());

        // 대화 생성
        DialogueConversation conversation = DialogueConversation.builder()
                .user(user)
                .book(book)
                .title(request.getTitle())
                .content(request.getContent())
                .aiQuestion(request.getAiQuestion())
                .build();

        // 제목 자동 생성
        conversation.generateTitleIfEmpty();

        // 감정 태그 추가
        for (String emotionCode : request.getEmotions()) {
            conversation.addEmotion(EmotionType.fromCode(emotionCode));
        }

        DialogueConversation saved = conversationRepository.save(conversation);
        log.info("대화 기록 저장 완료 - conversationId: {}", saved.getConversationId());

        return DialogueResponse.from(saved);
    }

    /**
     * 대화 기록 목록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public DialoguePageResponse getConversations(Long userId, int page, int size, Long bookId, List<String> emotions) {
        log.info("대화 기록 목록 조회 - userId: {}, page: {}, size: {}", userId, page, size);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<DialogueConversation> conversationPage;

        if (bookId != null) {
            // 특정 도서의 대화만 조회
            conversationPage = conversationRepository
                    .findByUserUserIdAndBookBookIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, bookId, pageable);
        } else if (emotions != null && !emotions.isEmpty()) {
            // 감정 필터링
            conversationPage = conversationRepository.findByUserIdAndEmotions(userId, emotions, pageable);
        } else {
            // 전체 조회
            conversationPage = conversationRepository
                    .findByUserUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, pageable);
        }

        Page<DialogueListResponse> responsePage = conversationPage.map(DialogueListResponse::from);
        return DialoguePageResponse.from(responsePage);
    }

    /**
     * 대화 기록 상세 조회
     */
    @Transactional(readOnly = true)
    public DialogueResponse getConversation(Long conversationId, Long userId) {
        log.info("대화 기록 상세 조회 - conversationId: {}, userId: {}", conversationId, userId);

        DialogueConversation conversation = conversationRepository
                .findByConversationIdAndUserUserIdAndIsDeletedFalse(conversationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIALOGUE_NOT_FOUND));

        return DialogueResponse.from(conversation);
    }

    /**
     * 대화 기록 수정
     */
    @Transactional
    public DialogueResponse updateConversation(Long conversationId, Long userId, DialogueUpdateRequest request) {
        log.info("대화 기록 수정 - conversationId: {}, userId: {}", conversationId, userId);

        DialogueConversation conversation = conversationRepository
                .findByConversationIdAndUserUserIdAndIsDeletedFalse(conversationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIALOGUE_NOT_FOUND));

        // 감정 타입 검증
        validateEmotions(request.getEmotions());

        // 내용 수정
        if (request.getTitle() != null) {
            conversation.setTitle(request.getTitle());
        }
        conversation.setContent(request.getContent());
        conversation.setAiQuestion(request.getAiQuestion());

        // 제목 자동 생성 (비어있는 경우)
        conversation.generateTitleIfEmpty();

        // 감정 태그 전체 교체
        conversation.replaceEmotions(request.getEmotions());

        DialogueConversation updated = conversationRepository.save(conversation);
        log.info("대화 기록 수정 완료 - conversationId: {}", conversationId);

        return DialogueResponse.from(updated);
    }

    /**
     * 대화 기록 삭제 (Soft Delete)
     */
    @Transactional
    public DialogueDeleteResponse deleteConversation(Long conversationId, Long userId) {
        log.info("대화 기록 삭제 - conversationId: {}, userId: {}", conversationId, userId);

        DialogueConversation conversation = conversationRepository
                .findByConversationIdAndUserUserIdAndIsDeletedFalse(conversationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIALOGUE_NOT_FOUND));

        // Soft delete
        conversation.setIsDeleted(true);
        conversationRepository.save(conversation);

        log.info("대화 기록 삭제 완료 - conversationId: {}", conversationId);

        return DialogueDeleteResponse.of(conversationId);
    }

    /**
     * 대화 기록 검색
     */
    @Transactional(readOnly = true)
    public DialogueSearchResponse searchConversations(
            Long userId,
            String keyword,
            LocalDate startDate,
            LocalDate endDate,
            List<String> emotions,
            int page,
            int size) {

        log.info("대화 기록 검색 - userId: {}, keyword: {}", userId, keyword);

        Pageable pageable = PageRequest.of(page - 1, size);

        // 날짜 변환
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        // 감정 필터 처리
        List<String> emotionFilter = (emotions != null && !emotions.isEmpty()) ? emotions : null;

        Page<DialogueConversation> conversationPage = conversationRepository.searchWithFilters(
                userId, keyword, startDateTime, endDateTime, emotionFilter, pageable);

        Page<DialogueListResponse> responsePage = conversationPage.map(DialogueListResponse::from);

        return DialogueSearchResponse.builder()
                .conversations(responsePage.getContent())
                .pagination(DialoguePageResponse.PaginationInfo.builder()
                        .currentPage(responsePage.getNumber() + 1)
                        .pageSize(responsePage.getSize())
                        .totalPages(responsePage.getTotalPages())
                        .totalElements(responsePage.getTotalElements())
                        .build())
                .searchCriteria(DialogueSearchResponse.SearchCriteria.builder()
                        .keyword(keyword)
                        .startDate(startDate)
                        .endDate(endDate)
                        .emotions(emotions)
                        .build())
                .build();
    }

    /**
     * 감정 타입 검증
     */
    private void validateEmotions(List<String> emotions) {
        if (emotions == null || emotions.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "감정 태그는 최소 1개 이상 선택해야 합니다");
        }

        for (String emotionCode : emotions) {
            try {
                EmotionType.fromCode(emotionCode);
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.INVALID_EMOTION_TYPE, "유효하지 않은 감정 타입: " + emotionCode);
            }
        }
    }
}
