package ac.kr.mjc.capstone.auth.oauth2;

import ac.kr.mjc.capstone.domain.user.entity.Role;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String provider = registrationId.toUpperCase();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        String providerId = oAuth2UserInfo.getId();
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();
        String imageUrl = oAuth2UserInfo.getImageUrl();

        log.info("OAuth2 로그인 시도 - Provider: {}, ProviderId: {}, Email: {}, Name: {}", 
                provider, providerId, email, name);

        // provider + providerId로 기존 사용자 조회
        Optional<UserEntity> existingUser = userRepository.findByProviderAndProviderId(provider, providerId);

        UserEntity user;
        if (existingUser.isPresent()) {
            // 기존 사용자 - 정보 업데이트
            user = existingUser.get();
            user.updateOAuthInfo(truncateName(name), imageUrl);
            log.info("기존 OAuth2 사용자 로그인: {}", user.getEmail());
        } else {
            // 신규 사용자 등록
            user = createOAuth2User(provider, providerId, email, name, imageUrl);
            userRepository.save(user);
            log.info("신규 OAuth2 사용자 등록: {}", user.getEmail());
        }

        return new CustomOAuth2User(
                user.getUserId(),
                user.getEmail(),
                user.getRole(),
                oAuth2User.getAttributes()
        );
    }

    private UserEntity createOAuth2User(String provider, String providerId, String email, String name, String imageUrl) {
        // 이메일이 없는 경우 임시 이메일 생성
        if (email == null || email.isEmpty()) {
            email = provider.toLowerCase() + "_" + providerId + "@social.user";
        }

        // 이름이 없는 경우 기본값 설정
        if (name == null || name.isEmpty()) {
            name = provider + "User";
        }

        // 닉네임 생성 (provider + 랜덤 문자열)
        String nickname = generateUniqueNickname(provider);

        return UserEntity.builder()
                .email(email)
                .username(truncateName(name))
                .password(UUID.randomUUID().toString())  // 소셜 로그인은 비밀번호 불필요
                .nickname(nickname)
                .profileImg(imageUrl)
                .provider(provider)
                .providerId(providerId)
                .role(Role.USER)
                .build();
    }

    private String truncateName(String name) {
        if (name == null) return "User";
        // username 필드가 20자 제한이므로 자르기
        return name.length() > 20 ? name.substring(0, 20) : name;
    }

    private String generateUniqueNickname(String provider) {
        String baseNickname = provider.substring(0, Math.min(provider.length(), 6));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        return baseNickname + "_" + randomSuffix;
    }
}
