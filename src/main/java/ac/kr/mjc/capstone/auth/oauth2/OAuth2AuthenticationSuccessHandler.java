package ac.kr.mjc.capstone.auth.oauth2;

import ac.kr.mjc.capstone.auth.entity.RefreshToken;
import ac.kr.mjc.capstone.auth.repository.RefreshTokenRepository;
import ac.kr.mjc.capstone.auth.service.JwtService;
import ac.kr.mjc.capstone.global.config.JwtProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Value("${app.frontend-url:http://localhost:8080}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Long userId = oAuth2User.getUserId();
        String email = oAuth2User.getEmail();

        // JWT 토큰 생성
        String accessToken = jwtService.generateAccessToken(userId, email);
        String refreshToken = jwtService.generateRefreshToken(userId);

        // RefreshToken DB 저장
        saveOrUpdateRefreshToken(userId, refreshToken);

        log.info("OAuth2 로그인 성공 - UserId: {}, Email: {}", userId, email);

        // 프론트엔드로 리다이렉트 (토큰을 쿼리 파라미터로 전달)
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
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
