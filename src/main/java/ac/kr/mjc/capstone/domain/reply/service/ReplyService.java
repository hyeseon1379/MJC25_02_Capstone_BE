package ac.kr.mjc.capstone.domain.reply.service;

import ac.kr.mjc.capstone.domain.board.entity.BoardEntity;
import ac.kr.mjc.capstone.domain.board.repository.BoardRepository;
import ac.kr.mjc.capstone.domain.reply.dto.ReplyRequest;
import ac.kr.mjc.capstone.domain.reply.dto.ReplyResponse;
import ac.kr.mjc.capstone.domain.reply.dto.ReplyUpdateRequest;
import ac.kr.mjc.capstone.domain.reply.entity.ReplyEntity;
import ac.kr.mjc.capstone.domain.reply.repository.ReplyRepository;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 생성
     */
    @Transactional
    public ReplyResponse createReply(Long boardId, ReplyRequest request, Long userId) {
        log.info("댓글 생성 요청 - boardId: {}, userId: {}", boardId, userId);

        // 게시글 존재 확인
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. boardId: " + boardId));

        // 사용자 존재 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId: " + userId));

        // 댓글 엔티티 생성
        ReplyEntity reply = ReplyEntity.builder()
                .board(board)
                .user(user)
                .content(request.getContent())
                .build();

        ReplyEntity savedReply = replyRepository.save(reply);
        log.info("댓글 생성 완료 - replyId: {}", savedReply.getReplyId());

        return ReplyResponse.from(savedReply);
    }

    /**
     * 특정 게시글의 모든 댓글 조회
     */
    public List<ReplyResponse> getRepliesByBoardId(Long boardId) {
        log.info("게시글 댓글 조회 - boardId: {}", boardId);

        // 게시글 존재 확인
        if (!boardRepository.existsById(boardId)) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다. boardId: " + boardId);
        }

        List<ReplyEntity> replies = replyRepository.findByBoardIdWithUser(boardId);
        log.info("댓글 조회 완료 - 댓글 개수: {}", replies.size());

        return replies.stream()
                .map(ReplyResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public ReplyResponse updateReply(Long replyId, ReplyUpdateRequest request, Long userId) {
        log.info("댓글 수정 요청 - replyId: {}, userId: {}", replyId, userId);

        // 댓글 존재 확인
        ReplyEntity reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. replyId: " + replyId));

        // 작성자 확인
        if (!reply.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다.");
        }

        // 댓글 수정
        reply.updateContent(request.getContent());
        log.info("댓글 수정 완료 - replyId: {}", replyId);

        return ReplyResponse.from(reply);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteReply(Long replyId, Long userId) {
        log.info("댓글 삭제 요청 - replyId: {}, userId: {}", replyId, userId);

        // 댓글 존재 확인
        ReplyEntity reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. replyId: " + replyId));

        // 작성자 확인
        if (!reply.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다.");
        }

        replyRepository.delete(reply);
        log.info("댓글 삭제 완료 - replyId: {}", replyId);
    }

    /**
     * 특정 게시글의 댓글 개수 조회
     */
    public long countRepliesByBoardId(Long boardId) {
        return replyRepository.countByBoardBoardId(boardId);
    }
}
