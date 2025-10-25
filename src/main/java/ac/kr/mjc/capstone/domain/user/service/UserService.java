package ac.kr.mjc.capstone.domain.user.service;

import ac.kr.mjc.capstone.auth.repository.RefreshTokenRepository;
import ac.kr.mjc.capstone.domain.user.dto.PasswordResetRequest;
import ac.kr.mjc.capstone.domain.user.dto.SignupRequest;
import ac.kr.mjc.capstone.domain.user.dto.UserResponse;
import ac.kr.mjc.capstone.domain.user.dto.UserUpdateRequest;
import ac.kr.mjc.capstone.domain.user.dto.UserVerificationRequest;
import ac.kr.mjc.capstone.domain.user.entity.Role;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
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

        UserEntity savedUserEntity = userRepository.save(userEntity);
        log.info("User created: userId={}, email={}", savedUserEntity.getUserId(), savedUserEntity.getEmail());

        return UserResponse.from(savedUserEntity);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserInfo(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(userEntity);
    }

    @Transactional
    public boolean verifyUser(UserVerificationRequest request) {
        return userRepository.findByEmailAndUsername(request.getEmail(), request.getUsername())
                .isPresent();
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        // 사용자 인증
        UserEntity userEntity = userRepository.findByEmailAndUsername(request.getEmail(), request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_VERIFICATION_FAILED));

        // 비밀번호 업데이트
        userEntity.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(userEntity);

        log.info("Password reset: userId={}, email={}", userEntity.getUserId(), userEntity.getEmail());
    }

    @Transactional
    public UserResponse updateUserProfile(Long userId, UserUpdateRequest request) {
        // 사용자 조회
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 프로필 업데이트
        userEntity.updateProfile(
                request.getNickname(),
                request.getPhone(),
                request.getAddress(),
                request.getColor()
        );

        UserEntity updatedUser = userRepository.save(userEntity);
        log.info("User profile updated: userId={}, email={}", updatedUser.getUserId(), updatedUser.getEmail());

        return UserResponse.from(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId, String password) {
        // 사용자 조회
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        // RefreshToken 삭제
        refreshTokenRepository.deleteByUserId(userId);

        // 사용자 삭제
        userRepository.delete(userEntity);

        log.info("User deleted: userId={}, email={}", userId, userEntity.getEmail());
    }
}
