package ac.kr.mjc.capstone.domain.board.controller;

import ac.kr.mjc.capstone.domain.board.dto.BoardRequest;
import ac.kr.mjc.capstone.domain.board.dto.BoardResponse;
import ac.kr.mjc.capstone.domain.board.dto.BoardUpdateRequest;
import ac.kr.mjc.capstone.domain.board.service.BoardService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 생성
     * POST /api/boards
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BoardResponse>> createBoard(
            @Valid @RequestBody BoardRequest request,
            @AuthenticationPrincipal Long userId) {
        BoardResponse response = boardService.createBoard(request, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글 작성 성공", response));
    }

    /**
     * 게시글 단건 조회
     * GET /api/boards/{boardId}
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoard(@PathVariable Long boardId) {
        BoardResponse response = boardService.getBoard(boardId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 게시글 전체 조회
     * GET /api/boards
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getAllBoards() {
        List<BoardResponse> responses = boardService.getAllBoards();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 게시글 수정
     * PATCH /api/boards/{boardId}
     */
    @PatchMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponse>> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardUpdateRequest request,
            @AuthenticationPrincipal Long userId) {
        BoardResponse response = boardService.updateBoard(boardId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글 수정 성공", response));
    }

    /**
     * 게시글 삭제
     * DELETE /api/boards/{boardId}
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal Long userId) {
        boardService.deleteBoard(boardId, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글 삭제 성공", null));
    }
}

