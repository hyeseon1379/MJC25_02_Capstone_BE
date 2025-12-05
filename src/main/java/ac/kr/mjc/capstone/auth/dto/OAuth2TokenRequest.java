package ac.kr.mjc.capstone.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OAuth2 일회용 코드로 토큰 교환 요청 DTO
 */
@Getter
@NoArgsConstructor
public class OAuth2TokenRequest {

    @NotBlank(message = "인증 코드는 필수입니다")
    private String code;
}
