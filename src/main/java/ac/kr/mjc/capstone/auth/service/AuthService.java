package ac.kr.mjc.capstone.auth.service;

import ac.kr.mjc.capstone.auth.dto.LoginRequest;
import ac.kr.mjc.capstone.auth.dto.TokenResponse;
import ac.kr.mjc.capstone.auth.entity.RefreshToken;
import ac.kr.mjc.capstone.auth.repository.RefreshTokenRepository;
import ac.kr.mjc.capstone.domain.user.entity.User;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import ac.kr.mjc.capstone.global.config.JwtProperties;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 토큰 생성
        String accessToken = jwtService.generateAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getUserId());

        // RefreshToken 저장 또는 업데이트
        saveOrUpdateRefreshToken(user.getUserId(), refreshToken);

        log.info("User logged in: userId={}, email={}", user.getUserId(), user.getEmail());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
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
        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 새 AccessToken 생성
        String newAccessToken = jwtService.generateAccessToken(user.getUserId(), user.getEmail());

        log.info("Access token refreshed: userId={}", user.getUserId());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
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
