package ac.kr.mjc.capstone.domain.subscription.service;

import ac.kr.mjc.capstone.domain.subscription.dto.SubscriptionPlanResponse;
import ac.kr.mjc.capstone.domain.subscription.dto.SubscriptionRequest;
import ac.kr.mjc.capstone.domain.subscription.dto.SubscriptionResponse;
import ac.kr.mjc.capstone.domain.subscription.entity.PaymentStatus;
import ac.kr.mjc.capstone.domain.subscription.entity.Subscription;
import ac.kr.mjc.capstone.domain.subscription.entity.SubscriptionPlan;
import ac.kr.mjc.capstone.domain.subscription.entity.SubscriptionStatus;
import ac.kr.mjc.capstone.domain.subscription.repository.SubscriptionPlanRepository;
import ac.kr.mjc.capstone.domain.subscription.repository.SubscriptionRepository;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import ac.kr.mjc.capstone.global.util.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    /**
     * 활성 구독 플랜 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponse> getActivePlans() {
        log.info("활성 구독 플랜 목록 조회");
        return planRepository.findByIsActiveTrueOrderByPriceAsc().stream()
                .map(SubscriptionPlanResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 구독 플랜 상세 조회
     */
    @Transactional(readOnly = true)
    public SubscriptionPlanResponse getPlan(Long planId) {
        log.info("구독 플랜 상세 조회 - planId: {}", planId);
        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "플랜을 찾을 수 없습니다"));
        return SubscriptionPlanResponse.from(plan);
    }

    /**
     * 구독 생성
     */
    @Transactional
    public SubscriptionResponse createSubscription(Long userId, SubscriptionRequest request) {
        log.info("구독 생성 시작 - userId: {}, planId: {}", userId, request.getPlanId());

        // 사용자 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 플랜 확인
        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "플랜을 찾을 수 없습니다"));

        // 플랜 활성 여부 확인
        if (!plan.getIsActive()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "비활성 플랜입니다");
        }

        // 이미 동일 플랜의 활성 구독이 있는지 확인
        if (subscriptionRepository.existsByUserAndPlanAndStatus(user, plan, SubscriptionStatus.ACTIVE)) {
            throw new CustomException(ErrorCode.SUBSCRIPTION_ALREADY_EXISTS, "이미 해당 플랜을 구독 중입니다");
        }

        // 새 구독 생성
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setPaymentMethod(request.getPaymentMethod());
        subscription.setAmount(plan.getPrice());
        subscription.setAutoRenew(request.getAutoRenew());
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(plan.getDurationDays()));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setPaymentStatus(PaymentStatus.COMPLETED);

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        log.info("구독 저장 완료 - subscriptionId: {}", savedSubscription.getId());

        // 이메일 발송
        sendSubscriptionEmail(user, plan, savedSubscription);

        return SubscriptionResponse.from(savedSubscription);
    }

    /**
     * 구독 이메일 발송
     */
    private void sendSubscriptionEmail(UserEntity user, SubscriptionPlan plan, Subscription subscription) {
        try {
            emailService.sendSubscriptionConfirmation(
                    user.getEmail(),
                    plan.getName(),
                    subscription.getEndDate()
            );
            log.info("구독 확인 이메일 발송 완료 - email: {}", user.getEmail());
        } catch (Exception e) {
            log.error("구독 확인 이메일 발송 실패 - email: {}, error: {}", user.getEmail(), e.getMessage());
        }
    }

    /**
     * 사용자의 모든 구독 조회
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getMySubscriptions(Long userId) {
        log.info("사용자 구독 목록 조회 - userId: {}", userId);

        List<Subscription> subscriptions = subscriptionRepository.findAllByUserUserId(userId);

        return subscriptions.stream()
                .map(SubscriptionResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 활성 구독 조회
     */
    @Transactional(readOnly = true)
    public SubscriptionResponse getActiveSubscription(Long userId) {
        log.info("사용자 활성 구독 조회 - userId: {}", userId);

        Subscription subscription = subscriptionRepository
                .findByUserUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND, "활성 구독이 없습니다"));

        return SubscriptionResponse.from(subscription);
    }

    /**
     * 구독 상세 조회
     */
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscription(Long subscriptionId, Long userId) {
        log.info("구독 상세 조회 - subscriptionId: {}, userId: {}", subscriptionId, userId);

        Subscription subscription = subscriptionRepository.findByIdAndUserUserId(subscriptionId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        return SubscriptionResponse.from(subscription);
    }

    /**
     * 구독 취소
     */
    @Transactional
    public SubscriptionResponse cancelSubscription(Long subscriptionId, Long userId) {
        log.info("구독 취소 요청 - subscriptionId: {}, userId: {}", subscriptionId, userId);

        Subscription subscription = subscriptionRepository.findByIdAndUserUserId(subscriptionId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new CustomException(ErrorCode.SUBSCRIPTION_ALREADY_CANCELLED, "이미 취소된 구독입니다");
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);

        Subscription cancelledSubscription = subscriptionRepository.save(subscription);
        log.info("구독 취소 완료 - subscriptionId: {}", subscriptionId);

        // 구독 취소 이메일 발송
        try {
            emailService.sendSubscriptionCancellation(
                    subscription.getUser().getEmail(),
                    subscription.getPlan().getName()
            );
        } catch (Exception e) {
            log.error("구독 취소 이메일 발송 실패 - error: {}", e.getMessage());
        }
        
        return SubscriptionResponse.from(cancelledSubscription);
    }

    /**
     * 자동 갱신 설정 변경
     */
    @Transactional
    public SubscriptionResponse updateAutoRenew(Long subscriptionId, Long userId, boolean autoRenew) {
        log.info("자동 갱신 설정 변경 - subscriptionId: {}, userId: {}, autoRenew: {}", subscriptionId, userId, autoRenew);
        
        Subscription subscription = subscriptionRepository.findByIdAndUserUserId(subscriptionId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
        
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new CustomException(ErrorCode.SUBSCRIPTION_NOT_ACTIVE, "활성 상태의 구독만 자동 갱신 설정을 변경할 수 있습니다");
        }
        
        subscription.setAutoRenew(autoRenew);
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        
        log.info("자동 갱신 설정 변경 완료 - subscriptionId: {}, autoRenew: {}", subscriptionId, autoRenew);
        
        return SubscriptionResponse.from(updatedSubscription);
    }

    /**
     * 활성 구독 존재 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean hasActiveSubscription(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        return subscriptionRepository.existsByUserAndStatus(user, SubscriptionStatus.ACTIVE);
    }
}