package ac.kr.mjc.capstone.domain.share.controller;

import ac.kr.mjc.capstone.domain.share.dto.ShareBoardRequest;
import ac.kr.mjc.capstone.domain.share.dto.ShareBoardResponse;
import ac.kr.mjc.capstone.domain.share.dto.ShareStatusUpdateRequest;
import ac.kr.mjc.capstone.domain.share.entity.MeetStatus;
import ac.kr.mjc.capstone.domain.share.service.ShareBoardService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Share Board", description = "도서 나눔 게시판 API")
@Slf4j
@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class ShareBoardController {

    private final ShareBoardService shareBoardService;

    @Operation(summary = "목록 조회", description = "도서 나눔 게시글 목록을 조회합니다. 검색어와 상태 필터를 지원합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ShareBoardResponse>>> getShareBoardList(
            @PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "검색어 (제목+내용)") @RequestParam(required = false) String keyword,
            @Parameter(description = "상태 필터: SHARING, RESERVED, COMPLETED") @RequestParam(required = false) MeetStatus status) {

        Page<ShareBoardResponse> responses = shareBoardService.getShareBoardList(keyword, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", responses));
    }

    @Operation(summary = "상세 조회", description = "게시글 상세 정보를 조회합니다. 조회 시 조회수가 1 증가합니다.")
    @GetMapping("/{shareId}")
    public ResponseEntity<ApiResponse<ShareBoardResponse>> getShareBoard(
            @Parameter(description = "게시글 ID") @PathVariable Long shareId) {

        ShareBoardResponse response = shareBoardService.getShareBoard(shareId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    @Operation(
            summary = "글 작성",
            description = "새 도서 나눔 게시글을 작성합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> createShareBoard(
            @Parameter(description = "제목") @RequestPart("title") String title,
            @Parameter(description = "내용") @RequestPart("content") String content,
            @Parameter(description = "상태 (SHARING, RESERVED, COMPLETED)") @RequestPart(value = "meetStatus", required = false) String meetStatusStr,
            @Parameter(description = "이미지 파일") @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Long userId) {

        MeetStatus meetStatus = null;
        if (meetStatusStr != null && !meetStatusStr.isBlank()) {
            meetStatus = MeetStatus.valueOf(meetStatusStr.toUpperCase());
        }

        ShareBoardRequest request = ShareBoardRequest.builder()
                .title(title)
                .content(content)
                .meetStatus(meetStatus)
                .build();

        Long shareId = shareBoardService.createShareBoard(request, image, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 등록되었습니다.", new ShareIdResponse(shareId)));
    }

    @Operation(
            summary = "글 수정",
            description = "도서 나눔 게시글을 수정합니다. 작성자 본인만 수정 가능합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(value = "/{shareId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> updateShareBoard(
            @Parameter(description = "게시글 ID") @PathVariable Long shareId,
            @Parameter(description = "제목") @RequestPart(value = "title", required = false) String title,
            @Parameter(description = "내용") @RequestPart(value = "content", required = false) String content,
            @Parameter(description = "상태") @RequestPart(value = "meetStatus", required = false) String meetStatusStr,
            @Parameter(description = "이미지 파일") @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Long userId) {

        MeetStatus meetStatus = null;
        if (meetStatusStr != null && !meetStatusStr.isBlank()) {
            meetStatus = MeetStatus.valueOf(meetStatusStr.toUpperCase());
        }

        ShareBoardRequest request = ShareBoardRequest.builder()
                .title(title)
                .content(content)
                .meetStatus(meetStatus)
                .build();

        Long updatedId = shareBoardService.updateShareBoard(shareId, request, image, userId);

        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다.", new ShareIdResponse(updatedId)));
    }

    @Operation(
            summary = "글 삭제",
            description = "도서 나눔 게시글을 삭제합니다. 작성자 본인만 삭제 가능합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{shareId}")
    public ResponseEntity<ApiResponse<Void>> deleteShareBoard(
            @Parameter(description = "게시글 ID") @PathVariable Long shareId,
            @AuthenticationPrincipal Long userId) {

        shareBoardService.deleteShareBoard(shareId, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다.", null));
    }

    @Operation(
            summary = "상태 변경",
            description = "게시글의 나눔 상태를 변경합니다. 작성자 본인만 변경 가능합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{shareId}/status")
    public ResponseEntity<ApiResponse<ShareBoardResponse>> updateStatus(
            @Parameter(description = "게시글 ID") @PathVariable Long shareId,
            @Valid @RequestBody ShareStatusUpdateRequest request,
            @AuthenticationPrincipal Long userId) {

        ShareBoardResponse response = shareBoardService.updateStatus(shareId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("상태가 변경되었습니다.", response));
    }

    // Inner class for simple response
    private record ShareIdResponse(Long shareId) {}
}
