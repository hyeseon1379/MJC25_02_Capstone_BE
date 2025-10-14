package ac.kr.mjc.capstone.auth.controller;

import ac.kr.mjc.capstone.auth.dto.LoginRequest;
import ac.kr.mjc.capstone.auth.dto.RefreshTokenRequest;
import ac.kr.mjc.capstone.auth.dto.TokenResponse;
import ac.kr.mjc.capstone.auth.service.AuthService;
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
        TokenResponse tokenResponse = authService.login(request);
        return ApiResponse.success("로그인 성공", tokenResponse);
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
}
