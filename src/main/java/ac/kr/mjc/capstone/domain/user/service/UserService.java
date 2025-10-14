package ac.kr.mjc.capstone.domain.user.service;

import ac.kr.mjc.capstone.auth.repository.RefreshTokenRepository;
import ac.kr.mjc.capstone.domain.user.dto.PasswordResetRequest;
import ac.kr.mjc.capstone.domain.user.dto.SignupRequest;
import ac.kr.mjc.capstone.domain.user.dto.UserResponse;
import ac.kr.mjc.capstone.domain.user.dto.UserVerificationRequest;
import ac.kr.mjc.capstone.domain.user.entity.Role;
import ac.kr.mjc.capstone.domain.user.entity.User;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signup(SignupRequest request) {
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
        User user = User.builder()
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

        User savedUser = userRepository.save(user);
        log.info("User created: userId={}, email={}", savedUser.getUserId(), savedUser.getEmail());

        return UserResponse.from(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    @Transactional
    public boolean verifyUser(UserVerificationRequest request) {
        return userRepository.findByEmailAndUsername(request.getEmail(), request.getUsername())
                .isPresent();
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        // 사용자 인증
        User user = userRepository.findByEmailAndUsername(request.getEmail(), request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_VERIFICATION_FAILED));

        // 비밀번호 업데이트
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password reset: userId={}, email={}", user.getUserId(), user.getEmail());
    }

    @Transactional
    public void deleteUser(Long userId, String password) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        // RefreshToken 삭제
        refreshTokenRepository.deleteByUserId(userId);

        // 사용자 삭제
        userRepository.delete(user);

        log.info("User deleted: userId={}, email={}", userId, user.getEmail());
    }
}
