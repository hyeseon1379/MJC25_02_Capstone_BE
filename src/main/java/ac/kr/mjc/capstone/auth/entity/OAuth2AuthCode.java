package ac.kr.mjc.capstone.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * OAuth2 인증 후 발급되는 일회용 코드 엔티티
 * AccessToken URL 노출 방지를 위해 사용
 */
@Entity
@Table(name = "oauth2_auth_codes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OAuth2AuthCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean used;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public void markAsUsed() {
        this.used = true;
    }
}
