package ac.kr.mjc.capstone.domain.subscription.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionRequest {
    @NotNull(message = "패키지 ID는 필수입니다")
    private Long packageId;

    @NotNull(message = "결제 수단은 필수입니다")
    private String paymentMethod;

    private boolean autoRenew = false;
}