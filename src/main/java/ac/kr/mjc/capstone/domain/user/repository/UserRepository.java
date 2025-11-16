package ac.kr.mjc.capstone.domain.user.repository;

import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmailAndUsername(String email, String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    // UserRepository.java에 추가
    Optional<UserEntity> findByResetToken(String resetToken);
}
