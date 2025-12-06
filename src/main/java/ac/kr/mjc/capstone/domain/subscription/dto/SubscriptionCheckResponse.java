package ac.kr.mjc.capstone.domain.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionCheckResponse {
    private Boolean hasActiveSubscription;
}
