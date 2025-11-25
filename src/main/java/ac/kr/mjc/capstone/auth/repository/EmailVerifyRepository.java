package ac.kr.mjc.capstone.auth.repository;

import ac.kr.mjc.capstone.auth.entity.EmailVerify;
import ac.kr.mjc.capstone.auth.entity.RefreshToken;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerifyRepository extends JpaRepository<EmailVerify, Long> {

    @Modifying // 이 어노테이션이 대량 수정/삭제 쿼리임을 명시합니다.
    @Query("DELETE FROM EmailVerify e WHERE e.expiredAt < :expiredAt")
    int deleteByExpiredAtBefore(@Param("expiredAt") LocalDateTime expiredAt);

    Optional<EmailVerify> findByEmail(String email);
}
