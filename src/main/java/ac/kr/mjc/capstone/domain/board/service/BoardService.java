package ac.kr.mjc.capstone.domain.board.service;

import ac.kr.mjc.capstone.domain.board.dto.BoardRequest;
import ac.kr.mjc.capstone.domain.board.dto.BoardResponse;
import ac.kr.mjc.capstone.domain.board.dto.BoardUpdateRequest;
import ac.kr.mjc.capstone.domain.board.entity.BoardEntity;
import ac.kr.mjc.capstone.domain.board.repository.BoardRepository;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
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
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * Create - 게시글 생성
     */
    @Transactional
    public BoardResponse createBoard(BoardRequest request, Long userId) {
        // 사용자 조회
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 게시글 생성
        BoardEntity boardEntity = BoardEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(userEntity)
                .imageFile(request.getImageFile())
                .build();

        BoardEntity savedBoard = boardRepository.save(boardEntity);
        log.info("Board created: boardId={}, title={}, userId={}",
                savedBoard.getBoardId(), savedBoard.getTitle(), userId);

        return BoardResponse.from(savedBoard);
    }

    /**
     * Read - 게시글 단건 조회
     */
    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long boardId) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        log.info("Board retrieved: boardId={}, title={}", boardId, boardEntity.getTitle());
        return BoardResponse.from(boardEntity);
    }

    /**
     * Read - 게시글 전체 조회
     */
    @Transactional(readOnly = true)
    public Page<BoardResponse> getAllBoards(Pageable pageable) {
        Page<BoardEntity> boards = boardRepository.findAll(pageable);
        log.info("Total boards retrieved: {}", boards.getTotalElements());

        return boards.map(BoardResponse::from);
    }

    /**
     * Update - 게시글 수정
     */
    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardUpdateRequest request, Long userId) {
        // 게시글 조회
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // 작성자 본인 확인
        if (!boardEntity.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 게시글 수정
        boardEntity.updateBoard(
                request.getTitle(),
                request.getContent(),
                request.getImageFile()
        );

        log.info("Board updated: boardId={}, title={}, userId={}",
                boardId, boardEntity.getTitle(), userId);

        return BoardResponse.from(boardEntity);
    }

    /**
     * Delete - 게시글 삭제
     */
    @Transactional
    public void deleteBoard(Long boardId, Long userId) {
        // 게시글 조회
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // 작성자 본인 확인
        if (!boardEntity.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        boardRepository.delete(boardEntity);
        log.info("Board deleted: boardId={}, userId={}", boardId, userId);
    }
}

