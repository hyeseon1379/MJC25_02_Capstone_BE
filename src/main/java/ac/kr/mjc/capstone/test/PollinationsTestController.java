package ac.kr.mjc.capstone.test;

import ac.kr.mjc.capstone.global.util.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@Tag(name = "Pollinations Test", description = "Pollinations.ai 무료 이미지 생성 테스트")
@RestController
@RequestMapping("/api/test/pollinations")
@RequiredArgsConstructor
public class PollinationsTestController {

    private final WebClient webClient;
    private final GeminiService geminiService;

    @Value("${file.contest-image-dir:uploads/contest-images}")
    private String contestImageDir;

    // 요청 DTO
    @lombok.Data
    public static class MultipleRequest {
        private List<String> texts;
    }

    @Operation(summary = "Pollinations 이미지 생성 테스트", description = "한글 텍스트 → Gemini로 영어 프롬프트 생성 → Pollinations로 이미지 생성")
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generate(@RequestBody Map<String, String> request) {
        String koreanText = request.get("text");

        if (koreanText == null || koreanText.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "text 필드가 필요합니다."
            ));
        }

        try {
            log.info("=== 이미지 생성 시작 ===");
            log.info("1. 입력 텍스트: {}", koreanText);
            long totalStart = System.currentTimeMillis();

            // 1단계: Gemini로 한글 → 영어 프롬프트 변환
            long promptStart = System.currentTimeMillis();
            String englishPrompt = geminiService.generateImagePrompt(koreanText);
            long promptElapsed = System.currentTimeMillis() - promptStart;
            log.info("2. 영어 프롬프트 생성 완료 ({}ms): {}", promptElapsed, englishPrompt);

            // 2단계: Pollinations로 이미지 생성
            long imageStart = System.currentTimeMillis();
            String encodedPrompt = URLEncoder.encode(englishPrompt, StandardCharsets.UTF_8);
            String apiUrl = "https://image.pollinations.ai/prompt/" + encodedPrompt + "?width=1024&height=1024&nologo=true";
            log.info("3. Pollinations API 호출: {}", apiUrl);

            byte[] imageBytes = webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            long imageElapsed = System.currentTimeMillis() - imageStart;
            log.info("4. 이미지 생성 완료 ({}ms)", imageElapsed);

            if (imageBytes == null || imageBytes.length == 0) {
                throw new RuntimeException("이미지 데이터가 비어있습니다.");
            }

            // 파일 형식 확인 (magic bytes)
            String fileExt = "jpg";
            if (imageBytes.length > 4) {
                if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == (byte) 0x50) {
                    fileExt = "png";
                } else if (imageBytes[0] == (byte) 0x52 && imageBytes[1] == (byte) 0x49) {
                    fileExt = "webp";
                }
            }

            // 3단계: 파일 저장
            Path dirPath = Paths.get(contestImageDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String fileName = "pollinations_" + UUID.randomUUID().toString().substring(0, 8) + "." + fileExt;
            Path filePath = Paths.get(contestImageDir, fileName);
            Files.write(filePath, imageBytes);

            long totalElapsed = System.currentTimeMillis() - totalStart;
            log.info("5. 저장 완료: {} (총 소요시간: {}ms)", filePath, totalElapsed);
            log.info("=== 이미지 생성 완료 ===");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "이미지 생성 완료!",
                    "koreanText", koreanText,
                    "englishPrompt", englishPrompt,
                    "imagePath", filePath.toString(),
                    "promptGenerationMs", promptElapsed,
                    "imageGenerationMs", imageElapsed,
                    "totalElapsedMs", totalElapsed,
                    "totalElapsedSeconds", totalElapsed / 1000.0,
                    "fileSizeKB", imageBytes.length / 1024
            ));

        } catch (Exception e) {
            log.error("이미지 생성 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "이미지 생성 실패: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "연속 생성 테스트 (4장)", description = "월간 우승 시뮬레이션 - 한글 → 영어 프롬프트 → 이미지 4장 연속 생성")
    @PostMapping("/generate-multiple")
    public ResponseEntity<Map<String, Object>> generateMultiple(@RequestBody MultipleRequest request) {
        List<String> texts = request.getTexts();

        if (texts == null || texts.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "texts 배열이 필요합니다."
            ));
        }

        log.info("=== 연속 {}장 생성 시작 ===", texts.size());
        long totalStart = System.currentTimeMillis();

        List<Map<String, Object>> results = new ArrayList<>();

        for (int i = 0; i < texts.size(); i++) {
            String koreanText = texts.get(i);
            log.info("[{}/{}] 생성 시작: {}", i + 1, texts.size(), koreanText);

            long start = System.currentTimeMillis();

            try {
                // 1단계: Gemini로 한글 → 영어 프롬프트 변환
                String englishPrompt = geminiService.generateImagePrompt(koreanText);
                log.info("[{}/{}] 영어 프롬프트: {}", i + 1, texts.size(), englishPrompt);

                // 2단계: Pollinations로 이미지 생성
                String encodedPrompt = URLEncoder.encode(englishPrompt, StandardCharsets.UTF_8);
                String apiUrl = "https://image.pollinations.ai/prompt/" + encodedPrompt + "?width=1024&height=1024&nologo=true";

                byte[] imageBytes = webClient.get()
                        .uri(apiUrl)
                        .retrieve()
                        .bodyToMono(byte[].class)
                        .block();

                long elapsed = System.currentTimeMillis() - start;

                // 파일 형식 감지
                String fileExt = "jpg";
                if (imageBytes != null && imageBytes.length > 4) {
                    if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == (byte) 0x50) {
                        fileExt = "png";
                    } else if (imageBytes[0] == (byte) 0x52 && imageBytes[1] == (byte) 0x49) {
                        fileExt = "webp";
                    }
                }

                // 저장
                Path dirPath = Paths.get(contestImageDir);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }
                String fileName = "pollinations_multi_" + (i + 1) + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + fileExt;
                Path filePath = Paths.get(contestImageDir, fileName);
                Files.write(filePath, imageBytes);

                results.add(Map.of(
                        "index", i + 1,
                        "koreanText", koreanText,
                        "englishPrompt", englishPrompt,
                        "success", true,
                        "elapsedMs", elapsed,
                        "filePath", filePath.toString()
                ));

                log.info("[{}/{}] 완료! {}초", i + 1, texts.size(), elapsed / 1000.0);

            } catch (Exception e) {
                long elapsed = System.currentTimeMillis() - start;
                results.add(Map.of(
                        "index", i + 1,
                        "koreanText", koreanText,
                        "success", false,
                        "error", e.getMessage(),
                        "elapsedMs", elapsed
                ));
                log.error("[{}/{}] 실패: {}", i + 1, texts.size(), e.getMessage());
            }
        }

        long totalElapsed = System.currentTimeMillis() - totalStart;
        log.info("=== 전체 완료! 총 소요시간: {}초 ===", totalElapsed / 1000.0);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "totalElapsedMs", totalElapsed,
                "totalElapsedSeconds", totalElapsed / 1000.0,
                "results", results
        ));
    }
}
