package ac.kr.mjc.capstone.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "email_verify")
@Builder
public class EmailVerify {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verify_id")
    private Long verifyId;

    @Column(name = "email")
    private String email;

    @Column(name = "code")
    private String code;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

}
