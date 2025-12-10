package ac.kr.mjc.capstone.domain.subscription.dto;

import ac.kr.mjc.capstone.domain.subscription.entity.Subscription;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class SubscriptionResponse {
    private Long subscriptionId;
    private Long userId;
    private String username;
    private String userEmail;
    private Long planId;
    private String planName;
    private String planDescription;
    private String targetAge;
    private BigDecimal amount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean autoRenew;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime createdAt;

    public static SubscriptionResponse from(Subscription subscription) {
        return SubscriptionResponse.builder()
                .subscriptionId(subscription.getId())
                .userId(subscription.getUser().getUserId())
                .username(subscription.getUser().getUsername())
                .userEmail(subscription.getUser().getEmail())
                .planId(subscription.getPlan().getId())
                .planName(subscription.getPlan().getName())
                .planDescription(subscription.getPlan().getDescription())
                .targetAge(subscription.getPlan().getTargetAge())
                .amount(subscription.getAmount())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .autoRenew(subscription.getAutoRenew())
                .status(subscription.getStatus().name())
                .paymentMethod(subscription.getPaymentMethod())
                .paymentStatus(subscription.getPaymentStatus().name())
                .createdAt(subscription.getCreateAt())
                .build();
    }
}
