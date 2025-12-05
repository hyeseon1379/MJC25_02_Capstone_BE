package ac.kr.mjc.capstone.auth.service;

import ac.kr.mjc.capstone.auth.entity.OAuth2AuthCode;
import ac.kr.mjc.capstone.auth.repository.OAuth2AuthCodeRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuth2AuthCodeService {

    private final OAuth2AuthCodeRepository authCodeRepository;
    private static final int CODE_EXPIRY_SECONDS = 60; // 1분 (일회용 코드는 짧은 수명)

    /**
     * 일회용 인증 코드 생성
     */
    @Transactional
    public String generateAuthCode(Long userId, String email) {
        String code = generateSecureCode();

        OAuth2AuthCode authCode = OAuth2AuthCode.builder()
                .code(code)
                .userId(userId)
                .email(email)
                .expiryDate(LocalDateTime.now().plusSeconds(CODE_EXPIRY_SECONDS))
                .used(false)
                .build();

        authCodeRepository.save(authCode);
        log.debug("OAuth2 일회용 코드 생성 - UserId: {}", userId);

        return code;
    }

    /**
     * 일회용 코드 검증 및 사용자 정보 반환
     */
    @Transactional
    public OAuth2AuthCode validateAndConsumeCode(String code) {
        OAuth2AuthCode authCode = authCodeRepository.findByCodeAndUsedFalse(code)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        if (authCode.isExpired()) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        // 코드를 사용 처리 (일회용)
        authCode.markAsUsed();

        log.debug("OAuth2 일회용 코드 사용 - UserId: {}", authCode.getUserId());
        return authCode;
    }

    /**
     * 만료되거나 사용된 코드 정리 (매 10분마다 실행)
     */
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void cleanupExpiredCodes() {
        int deleted = authCodeRepository.deleteExpiredOrUsedCodes(LocalDateTime.now());
        if (deleted > 0) {
            log.info("만료/사용된 OAuth2 코드 {} 개 삭제", deleted);
        }
    }

    /**
     * 안전한 랜덤 코드 생성 (URL-safe Base64, 32자)
     */
    private String generateSecureCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
