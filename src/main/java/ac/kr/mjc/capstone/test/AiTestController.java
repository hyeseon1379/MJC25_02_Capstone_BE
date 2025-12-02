package ac.kr.mjc.capstone.test;

import ac.kr.mjc.capstone.global.util.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Tag(name = "AI Test", description = "AI 이미지 생성 테스트 API")
@RestController
@RequestMapping("/api/test/ai")
@RequiredArgsConstructor
public class AiTestController {

    private final GeminiService geminiService;

    @Value("${file.contest-image-dir:uploads/contest-images}")
    private String contestImageDir;

    @Operation(summary = "이미지 생성 테스트", description = "한글 텍스트로 이미지 생성 테스트")
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> testGenerateImage(@RequestBody Map<String, String> request) {
        String koreanText = request.get("text");
        
        if (koreanText == null || koreanText.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "text 필드가 필요합니다."
            ));
        }

        try {
            log.info("테스트 이미지 생성 시작: {}", koreanText);

            // 1. 프롬프트 생성
            String prompt = geminiService.generateImagePrompt(koreanText);
            log.info("생성된 프롬프트: {}", prompt);

            // 2. 이미지 생성
            String base64Image = geminiService.generateImage(prompt);

            // 3. 파일 저장
            Path dirPath = Paths.get(contestImageDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String fileName = "test_" + UUID.randomUUID().toString().substring(0, 8) + ".png";
            Path filePath = Paths.get(contestImageDir, fileName);
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            Files.write(filePath, imageBytes);

            log.info("이미지 저장 완료: {}", filePath);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "prompt", prompt,
                "imagePath", filePath.toString(),
                "message", "이미지 생성 완료!"
            ));

        } catch (Exception e) {
            log.error("이미지 생성 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "이미지 생성 실패: " + e.getMessage()
            ));
        }
    }
}
