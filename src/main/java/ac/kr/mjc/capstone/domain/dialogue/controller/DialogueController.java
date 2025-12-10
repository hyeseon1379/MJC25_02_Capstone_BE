package ac.kr.mjc.capstone.domain.dialogue.controller;

import ac.kr.mjc.capstone.domain.dialogue.dto.*;
import ac.kr.mjc.capstone.domain.dialogue.service.DialogueService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Dialogue", description = "독후 활동(대화 기록) API")
@Slf4j
@RestController
@RequestMapping("/api/dialogue/conversations")
@RequiredArgsConstructor
public class DialogueController {

    private final DialogueService dialogueService;

    /**
     * 대화 기록 등록
     */
    @Operation(
            summary = "대화 기록 등록",
            description = "아이와 나눈 독서 대화를 저장합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<ApiResponse<DialogueResponse>> createConversation(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody DialogueCreateRequest request) {

        log.info("대화 기록 등록 API 호출 - userId: {}", userId);
        DialogueResponse response = dialogueService.createConversation(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("대화 기록이 저장되었습니다.", response));
    }

    /**
     * 대화 기록 목록 조회
     */
    @Operation(
            summary = "대화 기록 목록 조회",
            description = "사용자의 모든 대화 기록을 최신순으로 조회합니다. 페이징, 도서별/감정별 필터링을 지원합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<ApiResponse<DialoguePageResponse>> getConversations(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "특정 도서의 대화만 조회") @RequestParam(required = false) Long bookId,
            @Parameter(description = "감정 필터 (쉼표 구분)") @RequestParam(required = false) List<String> emotions) {

        log.info("대화 기록 목록 조회 API 호출 - userId: {}, page: {}, size: {}", userId, page, size);
        DialoguePageResponse response = dialogueService.getConversations(userId, page, size, bookId, emotions);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 대화 기록 상세 조회
     */
    @Operation(
            summary = "대화 기록 상세 조회",
            description = "특정 대화 기록의 전체 내용을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<DialogueResponse>> getConversation(
            @Parameter(description = "대화 기록 ID") @PathVariable Long conversationId,
            @AuthenticationPrincipal Long userId) {

        log.info("대화 기록 상세 조회 API 호출 - conversationId: {}, userId: {}", conversationId, userId);
        DialogueResponse response = dialogueService.getConversation(conversationId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 대화 기록 수정
     */
    @Operation(
            summary = "대화 기록 수정",
            description = "기존 대화 기록을 수정합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<DialogueResponse>> updateConversation(
            @Parameter(description = "대화 기록 ID") @PathVariable Long conversationId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody DialogueUpdateRequest request) {

        log.info("대화 기록 수정 API 호출 - conversationId: {}, userId: {}", conversationId, userId);
        DialogueResponse response = dialogueService.updateConversation(conversationId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("대화 기록이 수정되었습니다.", response));
    }

    /**
     * 대화 기록 삭제
     */
    @Operation(
            summary = "대화 기록 삭제",
            description = "대화 기록을 삭제합니다. (Soft Delete)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<DialogueDeleteResponse>> deleteConversation(
            @Parameter(description = "대화 기록 ID") @PathVariable Long conversationId,
            @AuthenticationPrincipal Long userId) {

        log.info("대화 기록 삭제 API 호출 - conversationId: {}, userId: {}", conversationId, userId);
        DialogueDeleteResponse response = dialogueService.deleteConversation(conversationId, userId);
        return ResponseEntity.ok(ApiResponse.success("대화 기록이 삭제되었습니다.", response));
    }

    /**
     * 대화 기록 검색
     */
    @Operation(
            summary = "대화 기록 검색",
            description = "제목, 내용, 날짜, 감정 태그로 대화 기록을 검색합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<DialogueSearchResponse>> searchConversations(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "검색 키워드 (제목, 내용)") @RequestParam(required = false) String keyword,
            @Parameter(description = "검색 시작일 (YYYY-MM-DD)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "검색 종료일 (YYYY-MM-DD)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "감정 필터 (쉼표 구분)") @RequestParam(required = false) List<String> emotions,
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {

        log.info("대화 기록 검색 API 호출 - userId: {}, keyword: {}", userId, keyword);
        DialogueSearchResponse response = dialogueService.searchConversations(
                userId, keyword, startDate, endDate, emotions, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
