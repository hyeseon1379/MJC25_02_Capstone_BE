package ac.kr.mjc.capstone.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SingupEmailCodeSendRequest {
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;

}
