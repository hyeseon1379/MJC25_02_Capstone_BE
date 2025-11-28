package ac.kr.mjc.capstone.global.config;

import ac.kr.mjc.capstone.auth.filter.JwtAuthenticationFilter;
import ac.kr.mjc.capstone.auth.oauth2.CustomOAuth2UserService;
import ac.kr.mjc.capstone.auth.oauth2.OAuth2AuthenticationFailureHandler;
import ac.kr.mjc.capstone.auth.oauth2.OAuth2AuthenticationSuccessHandler;
import ac.kr.mjc.capstone.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
        
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",             // 구버전
                                "/swagger-ui/**",               // 최신 UI 리소스
                                "/v3/api-docs/**",              // OpenAPI 스펙 JSON
                                "/swagger-resources/**",        // Swagger 리소스
                                "/webjars/**"                   // Swagger 의존 정적파일
                        ).permitAll()
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/users/signup",
                                "/api/users/verify",
                                "/api/users/reset-password"
                        ).permitAll()
                        // OAuth2 관련 엔드포인트 허용
                        .requestMatchers(
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 게시판 조회는 모든 사용자 허용 (GET 요청)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/boards", "/api/boards/*").permitAll()
                        // 공지사항 조회는 모든 사용자 허용 (GET 요청)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/notices", "/api/notices/**").permitAll()
                        // 댓글 조회는 모든 사용자 허용 (GET 요청)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/boards/*/replies", "/api/boards/*/replies/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 기본 폼 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization")
                        )
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
