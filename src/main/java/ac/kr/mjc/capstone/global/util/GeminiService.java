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

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String POLLINATIONS_IMAGE_URL = "https://image.pollinations.ai/prompt/";

    /**
     * 한글 글 내용을 바탕으로 영어 이미지 프롬프트 생성 (Groq Llama 사용)
     */
    public String generateImagePrompt(String koreanText) {
        String groqApiKey = aiConfig.getGroq() != null ? aiConfig.getGroq().getApiKey() : null;
        
        log.info("Groq API Key: {}", groqApiKey != null && !groqApiKey.isBlank() 
                ? "설정됨 (길이: " + groqApiKey.length() + ")" : "NULL");

        if (groqApiKey == null || groqApiKey.isBlank()) {
            log.warn("Groq API Key가 없어서 기본 프롬프트 사용");
            return generateDefaultPrompt(koreanText);
        }

        String systemPrompt = """
            You are an expert at creating image generation prompts for children's book illustrations.
            Convert the given Korean text into an English prompt.
            
            IMPORTANT STYLE REQUIREMENTS:
            - Soft, gentle watercolor illustration style
            - Natural and warm atmosphere like classic picture books
            - Simple and clean design, not overly cute or exaggerated
            - Muted, soft color palette (not too bright or saturated)
            - Cozy and calm mood, like Beatrix Potter or Studio Ghibli background art
            - Characters with natural proportions, NOT big anime eyes
            - Minimalist and elegant composition
            
            Output ONLY the English prompt with style keywords included, nothing else.
            Always start with: "Gentle watercolor children's book illustration, soft muted colors, simple elegant style,"
            """;

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", koreanText)
                ),
                "temperature", 0.7,
                "max_tokens", 200
        );

        try {
            Map<String, Object> response = webClient.post()
                    .uri(GROQ_API_URL)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + groqApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // 응답에서 텍스트 추출
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String generatedPrompt = (String) message.get("content");

            log.info("Groq 프롬프트 생성 완료: {}", generatedPrompt);
            return generatedPrompt.trim();

        } catch (Exception e) {
            log.error("Groq API 호출 실패: {}", e.getMessage());
            log.warn("기본 프롬프트로 대체합니다.");
            return generateDefaultPrompt(koreanText);
        }
    }

    /**
     * API 실패 시 기본 프롬프트 생성
     */
    private String generateDefaultPrompt(String koreanText) {
        String basePrompt = "Gentle watercolor children's book illustration, soft muted colors, " +
                "simple elegant style, warm cozy atmosphere, natural character proportions, " +
                "classic picture book art like Beatrix Potter, minimalist composition, ";
        
        String cleaned = koreanText
                .replaceAll("[0-9]+주차:?", "")
                .replaceAll(":", "")
                .trim();
        
        return basePrompt + cleaned;
    }

    /**
     * Pollinations API로 이미지 생성 후 Base64 데이터 반환
     */
    public String generateImage(String prompt) {
        return generateImage(prompt, null);
    }

    /**
     * Pollinations API로 이미지 생성 (seed로 스타일 통일 가능)
     */
    public String generateImage(String prompt, Long seed) {
        log.info("Pollinations 이미지 생성 시작 - 프롬프트: {}", prompt);

        try {
            String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8);
            
            // seed가 있으면 스타일 통일, 없으면 랜덤
            String seedParam = (seed != null) ? "&seed=" + seed : "";
            String apiUrl = POLLINATIONS_IMAGE_URL + encodedPrompt + 
                    "?width=1024&height=1024&nologo=true" + seedParam;
            
            log.info("Pollinations API URL: {}", apiUrl);

            byte[] imageBytes = webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            if (imageBytes == null || imageBytes.length == 0) {
                throw new RuntimeException("이미지 데이터가 비어있습니다.");
            }

            log.info("Pollinations 이미지 생성 성공 - 크기: {}KB", imageBytes.length / 1024);

            return Base64.getEncoder().encodeToString(imageBytes);

        } catch (Exception e) {
            log.error("Pollinations 이미지 생성 실패: {}", e.getMessage());
            throw new RuntimeException("이미지 생성 실패: " + e.getMessage(), e);
        }
    }
}
