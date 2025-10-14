package ac.kr.mjc.capstone.domain.user.controller;

import ac.kr.mjc.capstone.domain.user.dto.*;
import ac.kr.mjc.capstone.domain.user.service.UserService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        UserResponse userResponse = userService.signup(request);
        return ApiResponse.success("회원가입 성공", userResponse);
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo(@AuthenticationPrincipal Long userId) {
        UserResponse userResponse = userService.getUserInfo(userId);
        return ApiResponse.success(userResponse);
    }

    @PostMapping("/verify")
    public ApiResponse<Boolean> verifyUser(@Valid @RequestBody UserVerificationRequest request) {
        boolean isVerified = userService.verifyUser(request);
        return ApiResponse.success("사용자 인증 " + (isVerified ? "성공" : "실패"), isVerified);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        userService.resetPassword(request);
        return ApiResponse.success("비밀번호 재설정 성공");
    }

    @DeleteMapping
    public ApiResponse<Void> deleteUser(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserDeleteRequest request // @RequestParam -> @RequestBody
    ) {
        // DTO에서 비밀번호를 꺼내 서비스에 전달
        userService.deleteUser(userId, request.getPassword());
        return ApiResponse.success("회원탈퇴 성공");
    }
}
