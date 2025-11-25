package ac.kr.mjc.capstone.domain.notice.service;

import ac.kr.mjc.capstone.domain.notice.dto.NoticeRequest;
import ac.kr.mjc.capstone.domain.notice.dto.NoticeResponse;
import ac.kr.mjc.capstone.domain.notice.dto.NoticeUpdateRequest;
import ac.kr.mjc.capstone.domain.notice.entity.NoticeEntity;
import ac.kr.mjc.capstone.domain.notice.repository.NoticeRepository;
import ac.kr.mjc.capstone.domain.user.entity.Role;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import ac.kr.mjc.capstone.global.media.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    /**
     * Create - 공지사항 생성 (ADMIN만 가능)
     */
    @Transactional
    public NoticeResponse createNotice(NoticeRequest request, Long userId) {
        // 사용자 조회
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // ADMIN 권한 확인
        if (userEntity.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 이미지 조회 (imageId가 있는 경우)
        ImageFileEntity imageFileEntity = null;
        if (request.getImageId() != null) {
            imageFileEntity = fileRepository.findById(request.getImageId())
                    .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
        }

        // 공지사항 생성
        NoticeEntity noticeEntity = NoticeEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(userEntity)
                .fileEntity(imageFileEntity)
                .build();

        NoticeEntity savedNotice = noticeRepository.save(noticeEntity);
        log.info("Notice created: noticeId={}, title={}, userId={}",
                savedNotice.getNoticeId(), savedNotice.getTitle(), userId);

        return NoticeResponse.from(savedNotice);
    }

    /**
     * Read - 공지사항 단건 조회 (모든 사용자 가능)
     */
    @Transactional(readOnly = true)
    public NoticeResponse getNotice(Long noticeId) {
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        log.info("Notice retrieved: noticeId={}, title={}", noticeId, noticeEntity.getTitle());
        return NoticeResponse.from(noticeEntity);
    }

    /**
     * Read - 공지사항 전체 조회 (모든 사용자 가능, 최신순)
     */
    @Transactional(readOnly = true)
    public Page<NoticeResponse> getAllNotices(Pageable pageable) {
        Page<NoticeEntity> notices = noticeRepository.findAllByOrderByCreateAtDesc(pageable);
        log.info("Total notices retrieved: {}", notices.getTotalElements());

        return notices.map(NoticeResponse::from);
    }

    /**
     * Update - 공지사항 수정 (ADMIN만 가능)
     */
    @Transactional
    public NoticeResponse updateNotice(Long noticeId, NoticeUpdateRequest request, Long userId) {
        // 공지사항 조회
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        // 사용자 조회 및 ADMIN 권한 확인
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (userEntity.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 이미지 조회 (imageId가 있는 경우)
        ImageFileEntity imageFileEntity = null;
        if (request.getImageId() != null) {
            imageFileEntity = fileRepository.findById(request.getImageId())
                    .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
        }

        // 공지사항 수정
        noticeEntity.updateNotice(
                request.getTitle(),
                request.getContent(),
                imageFileEntity
        );

        log.info("Notice updated: noticeId={}, title={}, userId={}",
                noticeId, noticeEntity.getTitle(), userId);

        return NoticeResponse.from(noticeEntity);
    }

    /**
     * Delete - 공지사항 삭제 (ADMIN만 가능)
     */
    @Transactional
    public void deleteNotice(Long noticeId, Long userId) {
        // 공지사항 조회
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        // 사용자 조회 및 ADMIN 권한 확인
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (userEntity.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        noticeRepository.delete(noticeEntity);
        log.info("Notice deleted: noticeId={}, userId={}", noticeId, userId);
    }
}
