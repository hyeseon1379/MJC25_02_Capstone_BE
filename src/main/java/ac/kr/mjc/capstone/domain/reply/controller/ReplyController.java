package ac.kr.mjc.capstone.domain.reply.controller;

import ac.kr.mjc.capstone.domain.reply.dto.ReplyRequest;
import ac.kr.mjc.capstone.domain.reply.dto.ReplyResponse;
import ac.kr.mjc.capstone.domain.reply.dto.ReplyUpdateRequest;
import ac.kr.mjc.capstone.domain.reply.service.ReplyService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reply", description = "댓글 API (인증된 사용자만 작성/수정/삭제 가능, 조회는 모든 사용자 가능)")
@Slf4j
@RestController
@RequestMapping("/api/boards/{boardId}/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    /**
     * 댓글 생성
     * POST /api/boards/{boardId}/replies
     */
    @Operation(
            summary = "댓글 생성",
            description = "인증된 사용자만 댓글을 작성할 수 있습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ReplyResponse>> createReply(
            @PathVariable Long boardId,
            @Valid @RequestBody ReplyRequest request,
            @AuthenticationPrincipal Long userId) {
        ReplyResponse response = replyService.createReply(boardId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("댓글 작성 성공", response));
    }

    /**
     * 특정 게시글의 모든 댓글 조회
     * GET /api/boards/{boardId}/replies
     */
    @Operation(
            summary = "댓글 목록 조회",
            description = "모든 사용자가 특정 게시글의 댓글을 조회할 수 있습니다. (인증 불필요)"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReplyResponse>>> getReplies(@PathVariable Long boardId) {
        List<ReplyResponse> responses = replyService.getRepliesByBoardId(boardId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 댓글 수정
     * PATCH /api/boards/{boardId}/replies/{replyId}
     */
    @Operation(
            summary = "댓글 수정",
            description = "댓글 작성자만 수정할 수 있습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{replyId}")
    public ResponseEntity<ApiResponse<ReplyResponse>> updateReply(
            @PathVariable Long boardId,
            @PathVariable Long replyId,
            @Valid @RequestBody ReplyUpdateRequest request,
            @AuthenticationPrincipal Long userId) {
        ReplyResponse response = replyService.updateReply(replyId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("댓글 수정 성공", response));
    }

    /**
     * 댓글 삭제
     * DELETE /api/boards/{boardId}/replies/{replyId}
     */
    @Operation(
            summary = "댓글 삭제",
            description = "댓글 작성자만 삭제할 수 있습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{replyId}")
    public ResponseEntity<ApiResponse<Void>> deleteReply(
            @PathVariable Long boardId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal Long userId) {
        replyService.deleteReply(replyId, userId);
        return ResponseEntity.ok(ApiResponse.success("댓글 삭제 성공", null));
    }
}
