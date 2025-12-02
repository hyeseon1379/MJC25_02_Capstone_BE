package ac.kr.mjc.capstone.domain.subscription.repository;

import ac.kr.mjc.capstone.domain.subscription.entity.Subscription;
import ac.kr.mjc.capstone.domain.subscription.entity.SubscriptionStatus;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    // 특정 사용자의 특정 상태 구독이 존재하는지 확인
    boolean existsByUserAndStatus(UserEntity user, SubscriptionStatus status);
    
    // 특정 사용자의 모든 구독 조회
    List<Subscription> findAllByUserUserId(Long userId);
    
    // 특정 사용자의 활성 구독 조회
    Optional<Subscription> findByUserUserIdAndStatus(Long userId, SubscriptionStatus status);
    
    // 특정 사용자의 가장 최근 구독 조회
    @Query("SELECT s FROM Subscription s WHERE s.user.userId = :userId ORDER BY s.createAt DESC LIMIT 1")
    Optional<Subscription> findLatestByUserId(@Param("userId") Long userId);
    
    // 구독 ID와 사용자 ID로 조회 (권한 체크용)
    Optional<Subscription> findByIdAndUserUserId(Long subscriptionId, Long userId);
}