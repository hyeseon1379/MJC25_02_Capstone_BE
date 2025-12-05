package ac.kr.mjc.capstone.auth.oauth2;

import ac.kr.mjc.capstone.auth.service.OAuth2AuthCodeService;
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

/**
 * OAuth2 인증 성공 핸들러
 * 
 * 보안 개선: AccessToken을 URL에 직접 노출하지 않고,
 * 일회용 코드(Authorization Code)를 전달하여 프론트엔드에서 토큰 교환
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2AuthCodeService authCodeService;

    @Value("${app.frontend-url:http://localhost:5500}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Long userId = oAuth2User.getUserId();
        String email = oAuth2User.getEmail();

        // 일회용 코드 생성 (AccessToken 대신)
        String authCode = authCodeService.generateAuthCode(userId, email);

        log.info("OAuth2 로그인 성공 - UserId: {}", userId);

        // 프론트엔드로 리다이렉트 (일회용 코드만 전달 - 보안 강화)
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/login.html")
                .queryParam("code", authCode)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
