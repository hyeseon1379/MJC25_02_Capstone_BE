package ac.kr.mjc.capstone.domain.children.dto;

import ac.kr.mjc.capstone.domain.children.entity.ChildGender;
import ac.kr.mjc.capstone.domain.children.entity.ChildrenEntity;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
public class ChildrenResponse {
    private Long childId;
    private String childName;
    private LocalDate childBirth;
    private ChildGender gender;
    private Integer birthOrder;
    private String profileImg;
    private String color;

    public static ChildrenResponse from(ChildrenEntity user) {
        return ChildrenResponse.builder()
                .childId(user.getChildId())
                .childName(user.getChildName())
                .childBirth(user.getChildBirth())
                .birthOrder(user.getBirthOrder())
                .profileImg(user.getProfileImg())
                .color(user.getColor())
                .gender(user.getGender())
                .build();
    }
}
