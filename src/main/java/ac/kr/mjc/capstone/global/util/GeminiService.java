package ac.kr.mjc.capstone.global.util;

import ac.kr.mjc.capstone.global.config.AiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final WebClient webClient;
    private final AiConfig aiConfig;

    private static final String GEMINI_TEXT_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private static final String POLLINATIONS_IMAGE_URL = "https://image.pollinations.ai/prompt/";

    /**
     * 한글 글 내용을 바탕으로 영어 이미지 프롬프트 생성
     */
    public String generateImagePrompt(String koreanText) {
        log.info("Gemini API Key: {}", aiConfig.getGemini() != null ? 
                (aiConfig.getGemini().getApiKey() != null ? "설정됨 (길이: " + aiConfig.getGemini().getApiKey().length() + ")" : "NULL") 
                : "Gemini config NULL");
        
        String requestPrompt = """
            다음 한글 글을 바탕으로 이미지 생성에 적합한 영어 프롬프트를 만들어줘.
            글의 분위기, 감정, 장면을 잘 표현해야 해.
            동화책 삽화 스타일로 따뜻하고 아름다운 이미지가 나오도록 해줘.
            프롬프트만 출력하고 다른 설명은 하지 마.
            
            글:
            %s
            """.formatted(koreanText);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", requestPrompt)
                        ))
                )
        );

        try {
            Map<String, Object> response = webClient.post()
                    .uri(GEMINI_TEXT_URL + "?key=" + aiConfig.getGemini().getApiKey())
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // 응답에서 텍스트 추출
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String generatedPrompt = (String) parts.get(0).get("text");

            log.info("Generated prompt: {}", generatedPrompt);
            return generatedPrompt.trim();

        } catch (Exception e) {
            log.error("Gemini 프롬프트 생성 API 호출 실패: {}", e.getMessage());
            throw new RuntimeException("프롬프트 생성 실패", e);
        }
    }

    /**
     * Pollinations API로 이미지 생성 후 Base64 데이터 반환
     * (무료, Rate Limit 여유로움)
     */
    public String generateImage(String prompt) {
        log.info("Pollinations 이미지 생성 시작 - 프롬프트: {}", prompt);

        try {
            // 1. URL 인코딩
            String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8);
            String apiUrl = POLLINATIONS_IMAGE_URL + encodedPrompt + "?width=1024&height=1024&nologo=true";
            log.info("Pollinations API URL: {}", apiUrl);

            // 2. 이미지 바이트 배열로 받기
            byte[] imageBytes = webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            if (imageBytes == null || imageBytes.length == 0) {
                throw new RuntimeException("이미지 데이터가 비어있습니다.");
            }

            log.info("Pollinations 이미지 생성 성공 - 크기: {}KB", imageBytes.length / 1024);

            // 3. Base64로 인코딩해서 반환 (기존 인터페이스 유지)
            return Base64.getEncoder().encodeToString(imageBytes);

        } catch (Exception e) {
            log.error("Pollinations 이미지 생성 실패: {}", e.getMessage());
            throw new RuntimeException("이미지 생성 실패: " + e.getMessage(), e);
        }
    }
}
