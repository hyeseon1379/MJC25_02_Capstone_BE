package ac.kr.mjc.capstone.domain.children.entity;

import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "children")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChildrenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_id")
    private Long childId;

    @Column(name = "child_name", nullable = false, length = 20)
    private String childName;

    @Column(name = "child_birth")
    private LocalDate childBirth;

    @Column(name = "birth_order")
    private Integer birthOrder;

    @Column(length = 10)
    private String color;

    @Column(name = "profile_img", length = 255)
    private String profileImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChildGender gender;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    // 업데이트 메서드 (Dirty Checking 사용)
    public void updateProfile(String childName, LocalDate childBirth, ChildGender gender, 
                             Integer birthOrder, String color, String profileImg) {
        if (childName != null) this.childName = childName;
        if (childBirth != null) this.childBirth = childBirth;
        if (gender != null) this.gender = gender;
        if (birthOrder != null) this.birthOrder = birthOrder;
        if (color != null) this.color = color;
        if (profileImg != null) this.profileImg = profileImg;
    }

}
