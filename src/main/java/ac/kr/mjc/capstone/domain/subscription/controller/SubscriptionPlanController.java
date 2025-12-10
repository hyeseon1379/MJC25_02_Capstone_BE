package ac.kr.mjc.capstone.domain.subscription.controller;

import ac.kr.mjc.capstone.domain.subscription.dto.SubscriptionPlanResponse;
import ac.kr.mjc.capstone.domain.subscription.service.SubscriptionService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Subscription Plan", description = "구독 플랜 조회 API")
@Slf4j
@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionService subscriptionService;

    /**
     * 활성 구독 플랜 목록 조회
     */
    @Operation(
            summary = "활성 구독 플랜 목록 조회",
            description = "현재 활성화된 모든 구독 플랜 목록을 조회합니다. 인증 불필요."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponse>>> getActivePlans() {
        log.info("활성 구독 플랜 목록 조회 API 호출");
        List<SubscriptionPlanResponse> plans = subscriptionService.getActivePlans();
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    /**
     * 구독 플랜 상세 조회
     */
    @Operation(
            summary = "구독 플랜 상세 조회",
            description = "특정 구독 플랜의 상세 정보를 조회합니다. 인증 불필요."
    )
    @GetMapping("/{planId}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> getPlan(
            @Parameter(description = "플랜 ID") @PathVariable Long planId) {
        log.info("구독 플랜 상세 조회 API 호출 - planId: {}", planId);
        SubscriptionPlanResponse plan = subscriptionService.getPlan(planId);
        return ResponseEntity.ok(ApiResponse.success(plan));
    }
}
