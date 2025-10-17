package ac.kr.mjc.capstone.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "사용자 이름을 입력해주세요")
    @Size(min = 2, max = 20, message = "사용자 이름은 2-20자 사이여야 합니다")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일은 YYYY-MM-DD 형식이어야 합니다")
    private String birth;

    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다")
    private String phone;

    @Size(max = 20, message = "닉네임은 최대 20자까지 가능합니다")
    private String nickname;

    @Size(max = 10, message = "색상 코드는 최대 10자까지 가능합니다")
    private String color;

    @Size(max = 255, message = "주소는 최대 255자까지 가능합니다")
    private String address;
}
