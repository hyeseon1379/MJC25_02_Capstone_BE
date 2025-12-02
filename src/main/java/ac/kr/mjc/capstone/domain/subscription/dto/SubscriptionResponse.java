package ac.kr.mjc.capstone.domain.subscription.dto;

import ac.kr.mjc.capstone.domain.subscription.entity.PaymentStatus;
import ac.kr.mjc.capstone.domain.subscription.entity.Subscription;
import ac.kr.mjc.capstone.domain.subscription.entity.SubscriptionStatus;
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
    private Long packageId;
    private String packageName;
    private BigDecimal amount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean autoRenew;
    private SubscriptionStatus status;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static SubscriptionResponse from(Subscription subscription) {
        return SubscriptionResponse.builder()
                .subscriptionId(subscription.getId())
                .userId(subscription.getUser().getUserId())
                .username(subscription.getUser().getUsername())
                .userEmail(subscription.getUser().getEmail())
                .packageId(subscription.getPackaze().getId())
                .packageName(subscription.getPackaze().getName())
                .amount(subscription.getAmount())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .autoRenew(subscription.getAutoRenew())
                .status(subscription.getStatus())
                .paymentMethod(subscription.getPaymentMethod())
                .paymentStatus(subscription.getPaymentStatus())
                .createAt(subscription.getCreateAt())
                .updateAt(subscription.getUpdateAt())
                .build();
    }
}
