package ac.kr.mjc.capstone.domain.subscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionRequest {

    @NotNull(message = "플랜 ID는 필수입니다")
    private Long planId;

    @NotBlank(message = "결제 수단은 필수입니다")
    private String paymentMethod;  // "CARD" 또는 "BANK_TRANSFER"

    private Boolean autoRenew = true;
}