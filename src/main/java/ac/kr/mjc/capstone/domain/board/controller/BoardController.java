package ac.kr.mjc.capstone.domain.board.controller;

import ac.kr.mjc.capstone.domain.board.dto.BoardRequest;
import ac.kr.mjc.capstone.domain.board.dto.BoardResponse;
import ac.kr.mjc.capstone.domain.board.dto.BoardUpdateRequest;
import ac.kr.mjc.capstone.domain.board.service.BoardService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Board", description = "게시판 API (인증된 사용자만 작성/수정/삭제 가능, 조회는 모든 사용자 가능)")
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
    @Operation(
            summary = "게시글 생성",
            description = "인증된 사용자만 게시글을 작성할 수 있습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
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
    @Operation(
            summary = "게시글 단건 조회",
            description = "모든 사용자가 특정 게시글을 조회할 수 있습니다. (인증 불필요)"
    )
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoard(@PathVariable Long boardId) {
        BoardResponse response = boardService.getBoard(boardId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 게시글 전체 조회
     * GET /api/boards
     */
    @Operation(
            summary = "게시글 전체 조회",
            description = "모든 사용자가 전체 게시글을 최신순으로 조회할 수 있습니다. (인증 불필요, 제목 검색)"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BoardResponse>>> getAllBoards(@PageableDefault(size = 10, direction = Sort.Direction.DESC) Pageable pageable,
                                                                         @RequestParam(name = "title", defaultValue = "") String title) {
        Page<BoardResponse> responses = boardService.findAllByTitleContaining(pageable, title);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 게시글 수정
     * PATCH /api/boards/{boardId}
     */
    @Operation(
            summary = "게시글 수정",
            description = "게시글 작성자만 수정할 수 있습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
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
    @Operation(
            summary = "게시글 삭제",
            description = "게시글 작성자만 삭제할 수 있습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal Long userId) {
        boardService.deleteBoard(boardId, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글 삭제 성공", null));
    }
}

