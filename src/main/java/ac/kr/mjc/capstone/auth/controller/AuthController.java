package ac.kr.mjc.capstone.auth.controller;

import ac.kr.mjc.capstone.auth.dto.*;
import ac.kr.mjc.capstone.auth.service.AuthService;
import ac.kr.mjc.capstone.global.error.LoginException;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        try{
            TokenResponse tokenResponse = authService.login(request);
            ResponseCookie responseCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                    .path("/")
                    .secure(true)                                // https 환경에서만 쿠키가 발동합니다.
                    .sameSite("None")                            // 동일 사이트과 크로스 사이트에 모두 쿠키 전송이 가능합니다
                    .httpOnly(true)                              // 브라우저에서 쿠키에 접근할 수 없도록 제한
                    .build();

            response.setHeader("Set-cookie", responseCookie.toString());
            return ApiResponse.success("로그인 성공", tokenResponse.getAccessToken());

        } catch (Exception e) {
            log.error("로그인 실패 : " + e.toString());
            throw new LoginException("GG");
        }

    }

    /**
     * OAuth2 일회용 코드를 토큰으로 교환
     * 보안 개선: URL에 AccessToken 노출 방지
     */
    @PostMapping("/oauth2/token")
    public ApiResponse<String> exchangeOAuth2Code(@Valid @RequestBody OAuth2TokenRequest request,
                                                   HttpServletResponse response) {
        TokenResponse tokenResponse = authService.exchangeOAuth2CodeForTokens(request.getCode());

        // RefreshToken을 HttpOnly 쿠키로 설정
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();

        response.setHeader("Set-Cookie", responseCookie.toString());
        return ApiResponse.success("OAuth2 토큰 발급 성공", tokenResponse.getAccessToken());
    }

    @PostMapping("/refresh")
    public ApiResponse<String> refresh(@Valid @RequestBody RefreshTokenRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = authService.refreshAccessToken(request.getRefreshToken());
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .path("/")
                .secure(true)                                // https 환경에서만 쿠키가 발동합니다.
                .sameSite("None")                            // 동일 사이트과 크로스 사이트에 모두 쿠키 전송이 가능합니다
                .httpOnly(true)                              // 브라우저에서 쿠키에 접근할 수 없도록 제한
                .build();

        response.setHeader("Set-Cookie", responseCookie.toString());
        return ApiResponse.success("토큰 갱신 성공", tokenResponse.getAccessToken());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal Long userId) {
        authService.logout(userId);
        return ApiResponse.success("로그아웃 성공");
    }

    // AuthController.java에 추가

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ApiResponse.success("비밀번호 재설정 이메일을 발송했습니다");
    }

    @PostMapping("/verify-code")
    public ApiResponse<VerifyCodeResponse> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        VerifyCodeResponse response = authService.verifyResetCode(request.getCode());
        return ApiResponse.success("인증 코드 검증 성공", response);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ResetPasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 비밀번호 확인이 일치하지 않습니다");
        }

        authService.resetPasswordWithToken(userId, request.getNewPassword());
        return ApiResponse.success("비밀번호가 성공적으로 재설정되었습니다");
    }

    @PostMapping("/singup/send-code")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody SingupEmailCodeSendRequest request) {
        authService.sinupEmailSendCode(request.getEmail());
        return ApiResponse.success("이메일 인증 코드를 전송했습니다");
    }

    @PostMapping("/singup/verify-code")
    public ApiResponse<Boolean> verifyCode(@Valid @RequestBody SingupVerifyCodeRequest request) {
        Boolean result = authService.sinupVerifyCode(request.getEmail(), request.getCode());
        if(result){
            return ApiResponse.success("인증에 성공했습니다.",result);
        }
        throw new IllegalArgumentException("인증에 실패했습니다.");
    }
}
