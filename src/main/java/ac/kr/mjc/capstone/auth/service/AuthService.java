package ac.kr.mjc.capstone.auth.service;

import ac.kr.mjc.capstone.auth.dto.*;
import ac.kr.mjc.capstone.auth.entity.EmailVerify;
import ac.kr.mjc.capstone.auth.entity.RefreshToken;
import ac.kr.mjc.capstone.auth.repository.EmailVerifyRepository;
import ac.kr.mjc.capstone.auth.repository.RefreshTokenRepository;
import ac.kr.mjc.capstone.domain.user.dto.SignupRequest;
import ac.kr.mjc.capstone.domain.user.entity.Role;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import ac.kr.mjc.capstone.global.config.JwtProperties;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import ac.kr.mjc.capstone.global.util.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final EmailService emailService;
    private final EmailVerifyRepository emailRepository;

    @Transactional
    public void forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 소셜 로그인 사용자 체크
        if (user.isSocialUser()) {
            throw new CustomException(ErrorCode.SOCIAL_USER_CANNOT_CHANGE_PASSWORD);
        }

        // 6자리 인증 코드 생성
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        String resetCode = String.valueOf(code);

        // 토큰 및 만료 시간 설정 (1시간)
        user.setResetToken(resetCode, LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // 이메일 발송
        String emailBody = "비밀번호 재설정을 위한 인증 코드입니다:\n\n" + resetCode +
                "\n\n이 코드는 1시간 동안 유효합니다.";
        emailService.sendEmail(user.getEmail(), "비밀번호 재설정 인증 코드", emailBody);

        log.info("Password reset code sent to: {}", email);
    }

    @Transactional(readOnly = true)
    public VerifyCodeResponse verifyResetCode(String code) {
        UserEntity user = userRepository.findByResetToken(code)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VERIFICATION_CODE));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.EXPIRED_VERIFICATION_CODE);
        }

        // 임시 토큰 발급
        String temporaryToken = jwtService.generateTemporaryToken(user.getUserId());

        log.info("Reset code verified for user: {}", user.getEmail());

        return VerifyCodeResponse.builder()
                .temporaryToken(temporaryToken)
                .build();
    }

    @Transactional
    public void sinupEmailSendCode(String email) {
        // 6자리 인증 코드 생성
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        String resetCode = String.valueOf(code);

        // 이메일 발송
        String emailBody = "이메일 인증을 위한 코드입니다:\n\n" + resetCode +
                "\n\n이 코드는 5분 동안 유효합니다.";
        emailService.sendEmail(email, "이메일 인증 코드", emailBody);

        EmailVerify emailVerify = emailRepository.findByEmail(email).orElse(null);

        if(emailVerify != null){
            emailRepository.save(EmailVerify.builder()
                    .verifyId(emailVerify.getVerifyId())
                    .code(resetCode)
                    .email(email)
                    .expiredAt(LocalDateTime.now().plusMinutes(5))
                    .build());
        }else {
            emailRepository.save(EmailVerify.builder()
                    .code(resetCode)
                    .email(email)
                    .expiredAt(LocalDateTime.now().plusMinutes(5))
                    .build());

        }


        log.info("Email verify code sent to: {}", email);
    }

    @Transactional(readOnly = true)
    public Boolean sinupVerifyCode(String email,String code) {
        EmailVerify emailVerify = emailRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VERIFICATION_CODE));

        if (emailVerify.getCode().equals(code)){
            log.info("Reset code verified for user: {}", email);
            return true;
        }
        return false;
    }

    @Transactional
    public void resetPasswordWithToken(Long userId, String newPassword) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 소셜 로그인 사용자 체크
        if (user.isSocialUser()) {
            throw new CustomException(ErrorCode.SOCIAL_USER_CANNOT_CHANGE_PASSWORD);
        }

        // 새 비밀번호 암호화 및 저장
        user.updatePassword(passwordEncoder.encode(newPassword));

        // 재설정 토큰 초기화
        user.clearResetToken();

        userRepository.save(user);

        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    @Transactional
    public void signup(SignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 사용자 이름 중복 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        // 생년월일 변환
        LocalDate birthDate = null;
        if (request.getBirth() != null && !request.getBirth().isEmpty()) {
            try {
                birthDate = LocalDate.parse(request.getBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
        }

        // 사용자 생성
        UserEntity userEntity = UserEntity.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .birth(birthDate)
                .phone(request.getPhone())
                .nickname(request.getNickname())
                .color(request.getColor())
                .address(request.getAddress())
                .role(Role.USER)
                .build();

        userRepository.save(userEntity);
        log.info("User registered: {}", userEntity.getEmail());
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 사용자 조회
        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 토큰 생성
        String accessToken = jwtService.generateAccessToken(userEntity.getUserId(), userEntity.getEmail());
        String refreshToken = jwtService.generateRefreshToken(userEntity.getUserId());

        // RefreshToken 저장 또는 업데이트
        saveOrUpdateRefreshToken(userEntity.getUserId(), refreshToken);

        log.info("User logged in: userId={}, email={}", userEntity.getUserId(), userEntity.getEmail());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public TokenResponse refreshAccessToken(String refreshToken) {
        // RefreshToken 검증
        jwtService.validateToken(refreshToken);

        // DB에서 RefreshToken 조회
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));

        // 만료 확인
        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        // 사용자 조회
        UserEntity userEntity = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 새 AccessToken 생성
        String newAccessToken = jwtService.generateAccessToken(userEntity.getUserId(), userEntity.getEmail());

        // 새 RefreshToken 생성
        String newRefreshToken = jwtService.generateRefreshToken(userEntity.getUserId());
        saveOrUpdateRefreshToken(userEntity.getUserId(), newRefreshToken);

        log.info("Access token refreshed: userId={}", userEntity.getUserId());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("User logged out: userId={}", userId);
    }
    
    private void saveOrUpdateRefreshToken(Long userId, String token) {
        LocalDateTime expiryDate = LocalDateTime.now()
                .plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000);

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        existingToken -> existingToken.updateToken(token, expiryDate),
                        () -> {
                            RefreshToken newToken = RefreshToken.builder()
                                    .userId(userId)
                                    .token(token)
                                    .expiryDate(expiryDate)
                                    .build();
                            refreshTokenRepository.save(newToken);
                        }
                );
    }
}
