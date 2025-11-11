package ac.kr.mjc.capstone.auth.controller;

import ac.kr.mjc.capstone.auth.dto.*;
import ac.kr.mjc.capstone.auth.service.AuthService;
import ac.kr.mjc.capstone.global.error.LoginException;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        try{
            TokenResponse tokenResponse = authService.login(request);
            return ApiResponse.success("로그인 성공", tokenResponse);

        } catch (Exception e) {
            log.error("로그인 실패 : " + e.toString());
            throw new LoginException("GG");
        }

    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse tokenResponse = authService.refreshAccessToken(request.getRefreshToken());
        return ApiResponse.success("토큰 갱신 성공", tokenResponse);
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
}
