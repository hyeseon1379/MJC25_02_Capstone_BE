package ac.kr.mjc.capstone.domain.user.repository;

import ac.kr.mjc.capstone.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailAndUsername(String email, String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
