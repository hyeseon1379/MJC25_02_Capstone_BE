package ac.kr.mjc.capstone.auth.repository;

import ac.kr.mjc.capstone.auth.entity.OAuth2AuthCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OAuth2AuthCodeRepository extends JpaRepository<OAuth2AuthCode, Long> {

    Optional<OAuth2AuthCode> findByCodeAndUsedFalse(String code);

    @Modifying
    @Query("DELETE FROM OAuth2AuthCode o WHERE o.expiryDate < :now OR o.used = true")
    int deleteExpiredOrUsedCodes(LocalDateTime now);
}
