package ac.kr.mjc.capstone.domain.subscription.repository;

import ac.kr.mjc.capstone.domain.subscription.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    
    List<SubscriptionPlan> findByIsActiveTrue();
    
    List<SubscriptionPlan> findByIsActiveTrueOrderByPriceAsc();
}
