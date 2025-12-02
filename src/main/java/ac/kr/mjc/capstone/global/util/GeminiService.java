package ac.kr.mjc.capstone.global.util;

import ac.kr.mjc.capstone.global.config.AiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final WebClient webClient;
    private final AiConfig aiConfig;

    private static final String GEMINI_TEXT_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private static final String GEMINI_IMAGE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent";

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
     * 이미지 생성 후 Base64 데이터 반환
     */
    public String generateImage(String prompt) {
        // 1. 요청 데이터 준비
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", "Generate an image: " + prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "responseModalities", List.of("TEXT", "IMAGE")
                )
        );

        // 2. 재시도 설정
        int maxRetries = 3;
        int retryCount = 0;
        int waitTime = 10000;  // 10초부터 시작

        // 3. 반복문 시작
        while (retryCount < maxRetries) {
            try {
                Map<String, Object> response = webClient.post()
                        .uri(GEMINI_IMAGE_URL + "?key=" + aiConfig.getGemini().getApiKey())
                        .header("Content-Type", "application/json")
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                // 응답 파싱
                if (response == null || !response.containsKey("candidates")) {
                    throw new RuntimeException("응답이 비어있거나 candidates가 없습니다.");
                }

                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (candidates.isEmpty()) throw new RuntimeException("candidates 리스트가 비어있습니다.");

                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

                // 이미지 데이터 찾기
                for (Map<String, Object> part : parts) {
                    if (part.containsKey("inlineData")) {
                        Map<String, Object> inlineData = (Map<String, Object>) part.get("inlineData");
                        String base64Data = (String) inlineData.get("data");
                        log.info("이미지 생성 성공 (시도 횟수: {})", retryCount + 1);
                        return base64Data;
                    }
                }
                throw new RuntimeException("이미지 데이터를 찾을 수 없습니다.");

            } catch (Exception e) {
                retryCount++;
                log.warn("이미지 생성 API 실패 (시도 {}/{}): {}. {}ms 후 재시도...",
                        retryCount, maxRetries, e.getMessage(), waitTime);

                if (retryCount >= maxRetries) {
                    log.error("최대 재시도 횟수 초과. 최종 실패.");
                    throw new RuntimeException("이미지 생성 최종 실패: " + e.getMessage(), e);
                }

                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("대기 중 인터럽트 발생");
                }

                waitTime *= 2;  // 10초 -> 20초 -> 40초
            }
        }
        throw new RuntimeException("알 수 없는 오류로 이미지 생성 실패");
    }
}
