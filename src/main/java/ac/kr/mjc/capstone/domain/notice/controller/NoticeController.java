package ac.kr.mjc.capstone.domain.notice.controller;

import ac.kr.mjc.capstone.domain.notice.dto.NoticeRequest;
import ac.kr.mjc.capstone.domain.notice.dto.NoticeResponse;
import ac.kr.mjc.capstone.domain.notice.dto.NoticeUpdateRequest;
import ac.kr.mjc.capstone.domain.notice.service.NoticeService;
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

@Tag(name = "Notice", description = "공지사항 API (ADMIN 전용 작성/수정/삭제, 모든 사용자 조회 가능)")
@Slf4j
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 생성 (ADMIN만 가능)
     * POST /api/notices
     */
    @Operation(
            summary = "공지사항 생성",
            description = "ADMIN 권한을 가진 사용자만 공지사항을 작성할 수 있습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<ApiResponse<NoticeResponse>> createNotice(
            @Valid @RequestBody NoticeRequest request,
            @AuthenticationPrincipal Long userId) {
        NoticeResponse response = noticeService.createNotice(request, userId);
        return ResponseEntity.ok(ApiResponse.success("공지사항 작성 성공", response));
    }

    /**
     * 공지사항 단건 조회 (모든 사용자 가능)
     * GET /api/notices/{noticeId}
     */
    @Operation(
            summary = "공지사항 단건 조회",
            description = "모든 사용자가 특정 공지사항을 조회할 수 있습니다. (인증 불필요)"
    )
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeResponse>> getNotice(@PathVariable Long noticeId) {
        NoticeResponse response = noticeService.getNotice(noticeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 공지사항 전체 조회 (모든 사용자 가능)
     * GET /api/notices
     */
    @Operation(
            summary = "공지사항 전체 조회",
            description = "모든 사용자가 전체 공지사항을 최신순으로 조회할 수 있습니다. (인증 불필요)"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NoticeResponse>>> getAllNotices(@PageableDefault(size = 10, direction = Sort.Direction.DESC) Pageable pageable,
                                                                           @RequestParam(name = "title", defaultValue = "") String title) {
        Page<NoticeResponse> responses = noticeService.getAllNotices(pageable, title);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 공지사항 수정 (ADMIN만 가능)
     * PATCH /api/notices/{noticeId}
     */
    @Operation(
            summary = "공지사항 수정",
            description = "ADMIN 권한을 가진 사용자만 공지사항을 수정할 수 있습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeResponse>> updateNotice(
            @PathVariable Long noticeId,
            @Valid @RequestBody NoticeUpdateRequest request,
            @AuthenticationPrincipal Long userId) {
        NoticeResponse response = noticeService.updateNotice(noticeId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("공지사항 수정 성공", response));
    }

    /**
     * 공지사항 삭제 (ADMIN만 가능)
     * DELETE /api/notices/{noticeId}
     */
    @Operation(
            summary = "공지사항 삭제",
            description = "ADMIN 권한을 가진 사용자만 공지사항을 삭제할 수 있습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal Long userId) {
        noticeService.deleteNotice(noticeId, userId);
        return ResponseEntity.ok(ApiResponse.success("공지사항 삭제 성공", null));
    }
}
