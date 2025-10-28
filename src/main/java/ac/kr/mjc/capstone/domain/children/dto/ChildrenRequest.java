package ac.kr.mjc.capstone.domain.children.dto;

import ac.kr.mjc.capstone.domain.children.entity.ChildGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ChildrenRequest {

    @NotBlank(message = "자녀 이름은 필수입니다")
    @Size(max = 20, message = "자녀 이름은 20자 이하여야 합니다")
    private String childName;

    @Past(message = "생년월일은 과거여야 합니다")
    private LocalDate childBirth;

    @NotNull(message = "성별은 필수입니다")
    private ChildGender gender;

    private Integer birthOrder;

    @Size(max = 10, message = "색상 코드는 10자 이하여야 합니다")
    private String color;

    @Size(max = 255, message = "프로필 이미지 URL은 255자 이하여야 합니다")
    private String profileImg;
}
