package ac.kr.mjc.capstone.domain.subscription.dto;

import ac.kr.mjc.capstone.domain.subscription.entity.SubscriptionPlan;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class SubscriptionPlanResponse {
    private Long planId;
    private String name;
    private String description;
    private String targetAge;
    private BigDecimal price;
    private Integer durationDays;
    private Boolean isActive;

    public static SubscriptionPlanResponse from(SubscriptionPlan plan) {
        return SubscriptionPlanResponse.builder()
                .planId(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .targetAge(plan.getTargetAge())
                .price(plan.getPrice())
                .durationDays(plan.getDurationDays())
                .isActive(plan.getIsActive())
                .build();
    }
}
