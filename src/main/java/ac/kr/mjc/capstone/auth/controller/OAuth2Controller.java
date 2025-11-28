package ac.kr.mjc.capstone.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/oauth2")
@Tag(name = "OAuth2", description = "소셜 로그인 API")
public class OAuth2Controller {

    @Value("${app.base-url:http://localhost:8082}")
    private String baseUrl;

    @Operation(summary = "소셜 로그인 URL 조회", description = "네이버, 카카오 소셜 로그인 URL을 반환합니다.")
    @GetMapping("/login-urls")
    public ResponseEntity<Map<String, String>> getLoginUrls() {
        Map<String, String> urls = new HashMap<>();
        urls.put("naver", baseUrl + "/oauth2/authorization/naver");
        urls.put("kakao", baseUrl + "/oauth2/authorization/kakao");
        return ResponseEntity.ok(urls);
    }

    @Operation(summary = "네이버 로그인 URL", description = "네이버 소셜 로그인 URL을 반환합니다.")
    @GetMapping("/naver")
    public ResponseEntity<Map<String, String>> getNaverLoginUrl() {
        Map<String, String> response = new HashMap<>();
        response.put("loginUrl", baseUrl + "/oauth2/authorization/naver");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "카카오 로그인 URL", description = "카카오 소셜 로그인 URL을 반환합니다.")
    @GetMapping("/kakao")
    public ResponseEntity<Map<String, String>> getKakaoLoginUrl() {
        Map<String, String> response = new HashMap<>();
        response.put("loginUrl", baseUrl + "/oauth2/authorization/kakao");
        return ResponseEntity.ok(response);
    }
}
