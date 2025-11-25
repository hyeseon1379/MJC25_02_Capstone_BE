package ac.kr.mjc.capstone.auth.repository;

import ac.kr.mjc.capstone.auth.entity.EmailVerify;
import ac.kr.mjc.capstone.auth.entity.RefreshToken;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerifyRepository extends JpaRepository<EmailVerify, Long> {
    @Modifying
    @Transactional
    void deleteByExpiredAtBefore(LocalDateTime dateTime);

    Optional<EmailVerify> findByEmail(String email);
}
