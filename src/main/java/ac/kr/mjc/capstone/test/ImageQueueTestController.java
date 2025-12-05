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
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Tag(name = "Image Queue Test", description = "백그라운드 이미지 생성 테스트 API")
@RestController
@RequestMapping("/api/test/queue")
@RequiredArgsConstructor
public class ImageQueueTestController {

    private final GeminiService geminiService;

    @Value("${file.contest-image-dir:uploads/contest-images}")
    private String contestImageDir;

    // 간단한 인메모리 큐 (테스트용)
    private static final List<Map<String, Object>> queue = Collections.synchronizedList(new ArrayList<>());

    @Operation(summary = "4주차 한번에 큐에 추가", description = "4개 요청을 큐에 넣고 백그라운드에서 60초 간격으로 처리")
    @PostMapping("/add-monthly")
    public ResponseEntity<Map<String, Object>> addMonthly(@RequestBody Map<String, List<String>> request) {
        List<String> texts = request.get("texts");

        if (texts == null || texts.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "texts 배열이 필요합니다."
            ));
        }

        // 큐에 추가
        for (String text : texts) {
            queue.add(Map.of(
                    "id", UUID.randomUUID().toString().substring(0, 8),
                    "text", text,
                    "status", "PENDING",
                    "createdAt", System.currentTimeMillis()
            ));
        }

        log.info("큐에 {}개 추가됨. 현재 대기: {}개", texts.size(), queue.size());

        // 백그라운드에서 처리 시작
        CompletableFuture.runAsync(this::processQueue);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", texts.size() + "개가 큐에 추가되었습니다. 백그라운드에서 60초 간격으로 처리됩니다.",
                "estimatedMinutes", texts.size(),
                "queueSize", queue.size()
        ));
    }

    @Operation(summary = "큐 상태 조회")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(Map.of(
                "queueSize", queue.size(),
                "queue", queue
        ));
    }

    @Operation(summary = "큐 초기화")
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearQueue() {
        queue.clear();
        return ResponseEntity.ok(Map.of("success", true, "message", "큐가 초기화되었습니다."));
    }

    /**
     * 백그라운드에서 60초 간격으로 큐 처리
     */
    private void processQueue() {
        log.info("=== 백그라운드 큐 처리 시작 ===");

        while (!queue.isEmpty()) {
            Map<String, Object> item = queue.get(0);
            String text = (String) item.get("text");
            String id = (String) item.get("id");

            log.info("처리 중 - ID: {}, 텍스트: {}", id, text);

            try {
                // 이미지 생성
                String prompt = "High quality children's book illustration, warm style, " + text;
                String base64Image = geminiService.generateImage(prompt);

                // 파일 저장
                Path dirPath = Paths.get(contestImageDir);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }

                String fileName = "queue_" + id + ".png";
                Path filePath = Paths.get(contestImageDir, fileName);
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                Files.write(filePath, imageBytes);

                log.info("완료 - ID: {}, 파일: {}", id, filePath);
                queue.remove(0);  // 성공하면 큐에서 제거

            } catch (Exception e) {
                log.error("실패 - ID: {}, 에러: {}", id, e.getMessage());
                queue.remove(0);  // 실패해도 일단 제거 (테스트용)
            }

            // 다음 처리 전 60초 대기 (Rate Limit 방지)
            if (!queue.isEmpty()) {
                log.info("다음 처리까지 60초 대기... (남은 큐: {}개)", queue.size());
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.info("=== 백그라운드 큐 처리 완료 ===");
    }
}
