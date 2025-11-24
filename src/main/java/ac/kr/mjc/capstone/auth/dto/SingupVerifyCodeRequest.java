package ac.kr.mjc.capstone.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SingupVerifyCodeRequest {

    @NotBlank(message = "이메일은 필수입니다")
    private String email;

    @NotBlank(message = "인증 코드는 필수입니다")
    private String code;
}