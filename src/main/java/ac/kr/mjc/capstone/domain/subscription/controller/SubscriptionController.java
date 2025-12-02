package ac.kr.mjc.capstone.domain.subscription.controller;

import ac.kr.mjc.capstone.domain.subscription.dto.SubscriptionRequest;
import ac.kr.mjc.capstone.domain.subscription.dto.SubscriptionResponse;
import ac.kr.mjc.capstone.domain.subscription.service.SubscriptionService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Subscription", description = "구독 API - 버튼 클릭시 구독 데이터 추가 및 이메일 발송")
@Slf4j
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;

    /**
     * 구독 생성 (버튼 클릭 시 호출)
     * 구독 테이블에 데이터 추가 후 사용자에게 확인 이메일 발송
     */
    @Operation(
            summary = "구독 생성",
            description = "버튼 클릭 시 구독 데이터를 생성하고 사용자에게 구독 확인 이메일을 발송합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> createSubscription(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody SubscriptionRequest request) {
        
        log.info("구독 생성 API 호출 - userId: {}", userId);
        SubscriptionResponse response = subscriptionService.createSubscription(userId, request);
        return ResponseEntity.ok(ApiResponse.success("구독이 완료되었습니다. 확인 이메일이 발송되었습니다.", response));
    }

    /**
     * 내 구독 목록 조회
     */
    @Operation(
            summary = "내 구독 목록 조회",
            description = "로그인한 사용자의 모든 구독 내역을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getMySubscriptions(
            @AuthenticationPrincipal Long userId) {
        
        log.info("내 구독 목록 조회 API 호출 - userId: {}", userId);
        List<SubscriptionResponse> responses = subscriptionService.getMySubscriptions(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 활성 구독 조회
     */
    @Operation(
            summary = "활성 구독 조회",
            description = "현재 활성화된 구독 정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getActiveSubscription(
            @AuthenticationPrincipal Long userId) {
        
        log.info("활성 구독 조회 API 호출 - userId: {}", userId);
        SubscriptionResponse response = subscriptionService.getActiveSubscription(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 구독 상세 조회
     */
    @Operation(
            summary = "구독 상세 조회",
            description = "특정 구독의 상세 정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{subscriptionId}")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getSubscription(
            @Parameter(description = "구독 ID") @PathVariable Long subscriptionId,
            @AuthenticationPrincipal Long userId) {
        
        log.info("구독 상세 조회 API 호출 - subscriptionId: {}, userId: {}", subscriptionId, userId);
        SubscriptionResponse response = subscriptionService.getSubscription(subscriptionId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 구독 취소
     */
    @Operation(
            summary = "구독 취소",
            description = "구독을 취소합니다. 취소 확인 이메일이 발송됩니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> cancelSubscription(
            @Parameter(description = "구독 ID") @PathVariable Long subscriptionId,
            @AuthenticationPrincipal Long userId) {
        
        log.info("구독 취소 API 호출 - subscriptionId: {}, userId: {}", subscriptionId, userId);
        SubscriptionResponse response = subscriptionService.cancelSubscription(subscriptionId, userId);
        return ResponseEntity.ok(ApiResponse.success("구독이 취소되었습니다.", response));
    }

    /**
     * 자동 갱신 설정 변경
     */
    @Operation(
            summary = "자동 갱신 설정 변경",
            description = "구독의 자동 갱신 여부를 변경합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{subscriptionId}/auto-renew")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> updateAutoRenew(
            @Parameter(description = "구독 ID") @PathVariable Long subscriptionId,
            @Parameter(description = "자동 갱신 여부") @RequestParam boolean autoRenew,
            @AuthenticationPrincipal Long userId) {
        
        log.info("자동 갱신 설정 변경 API 호출 - subscriptionId: {}, autoRenew: {}", subscriptionId, autoRenew);
        SubscriptionResponse response = subscriptionService.updateAutoRenew(subscriptionId, userId, autoRenew);
        return ResponseEntity.ok(ApiResponse.success("자동 갱신 설정이 변경되었습니다.", response));
    }

    /**
     * 활성 구독 존재 여부 확인
     */
    @Operation(
            summary = "활성 구독 존재 여부 확인",
            description = "현재 활성화된 구독이 있는지 확인합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> hasActiveSubscription(
            @AuthenticationPrincipal Long userId) {
        
        log.info("활성 구독 확인 API 호출 - userId: {}", userId);
        boolean hasSubscription = subscriptionService.hasActiveSubscription(userId);
        return ResponseEntity.ok(ApiResponse.success(hasSubscription));
    }
}