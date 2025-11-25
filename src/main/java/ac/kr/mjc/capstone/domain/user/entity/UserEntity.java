package ac.kr.mjc.capstone.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "profile_img", length = 60000)
    private String profileImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // 비밀번호 재설정용 필드 추가
    @Column(name = "reset_token", length = 10)
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = Role.USER;
        }
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateProfile(String nickname, String phone, String address, String color, String profileImg) {
        if (nickname != null) this.nickname = nickname;
        if (phone != null) this.phone = phone;
        if (address != null) this.address = address;
        if (color != null) this.color = color;
        if (profileImg != null) this.profileImg = profileImg;
    }

    // 비밀번호 재설정 토큰 설정
    public void setResetToken(String resetToken, LocalDateTime expiryDate) {
        this.resetToken = resetToken;
        this.resetTokenExpiry = expiryDate;
    }

    // 비밀번호 재설정 토큰 초기화
    public void clearResetToken() {
        this.resetToken = null;
        this.resetTokenExpiry = null;
    }
}