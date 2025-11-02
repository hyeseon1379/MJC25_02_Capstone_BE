package ac.kr.mjc.capstone.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(length = 20)
    private String phone;

    @Column(length = 20, unique = true)
    private String nickname;

    @Column(length = 10)
    private String color;

    @Column(length = 255)
    private String address;

    @Column(name = "profile_img", length = 255)
    private String profileImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = Role.USER;
        }
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateProfile(String nickname, String phone, String address, String color) {
        if (nickname != null) this.nickname = nickname;
        if (phone != null) this.phone = phone;
        if (address != null) this.address = address;
        if (color != null) this.color = color;
    }
}
