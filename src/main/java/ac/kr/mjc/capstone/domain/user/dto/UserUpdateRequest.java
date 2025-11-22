package ac.kr.mjc.capstone.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {

    @Size(max = 20, message = "닉네임은 20자 이하여야 합니다")
    private String nickname;
  @Pattern(regexp = "^01[0-9]-\\d{4}-\\d{4}$",
            message = "올바른 휴대폰 번호 형식이 아닙니다 (예: 010-1234-5678)")
    private String phone;

    @Size(max = 255, message = "주소는 255자 이하여야 합니다")
    private String address;

    @Size(max = 10, message = "색상 코드는 10자 이하여야 합니다")
    private String color;

    private String profileImg;
}
