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

    public static ChildrenResponse from(ChildrenEntity child) {
        return ChildrenResponse.builder()
                .childId(child.getChildId())
                .childName(child.getChildName())
                .childBirth(child.getChildBirth())
                .birthOrder(child.getBirthOrder())
                .profileImg(child.getProfileImg())
                .color(child.getColor())
                .gender(child.getGender())
                .build();
    }
}
