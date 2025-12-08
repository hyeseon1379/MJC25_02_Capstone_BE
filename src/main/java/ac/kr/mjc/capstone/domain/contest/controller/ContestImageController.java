package ac.kr.mjc.capstone.domain.contest.controller;

import ac.kr.mjc.capstone.domain.contest.dto.ContestResultResponse;
import ac.kr.mjc.capstone.domain.contest.dto.ImageGenerationJob;
import ac.kr.mjc.capstone.domain.contest.entity.ContestResult;
import ac.kr.mjc.capstone.domain.contest.service.impl.ContestImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Contest Image", description = "대회 AI 이미지 생성 API (비동기)")
@RestController
@RequestMapping("/api/admin/contest")
@RequiredArgsConstructor
public class ContestImageController {

    private final ContestImageService contestImageService;

    @Operation(
            summary = "대회 이미지 생성 시작 (비동기)", 
            description = "특정 대회의 4개 라운드 1등 글로 AI 이미지 생성을 비동기로 시작합니다.\n\n" +
                    "- 즉시 jobId를 반환하고 백그라운드에서 이미지 생성 진행\n" +
                    "- 진행 상황은 GET /status/{jobId} 로 확인\n" +
                    "- 이미 진행 중인 작업이 있으면 기존 jobId 반환"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작업 시작 성공"),
            @ApiResponse(responseCode = "404", description = "대회를 찾을 수 없음")
    })
    @PostMapping("/{contestId}/generate-images")
    public ResponseEntity<Map<String, Object>> startImageGeneration(
            @Parameter(description = "대회 ID", required = true) 
            @PathVariable Long contestId) {
        
        log.info("대회 이미지 생성 요청 (비동기): contestId={}", contestId);
        
        String jobId = contestImageService.startImageGenerationJob(contestId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "이미지 생성 작업이 시작되었습니다. jobId로 진행 상황을 확인하세요.",
                "jobId", jobId
        ));
    }

    @Operation(
            summary = "이미지 생성 진행 상황 조회", 
            description = "jobId로 이미지 생성 작업의 진행 상황을 조회합니다.\n\n" +
                    "**status 값:**\n" +
                    "- `PENDING`: 대기 중\n" +
                    "- `PROCESSING`: 처리 중 (currentRound에서 현재 라운드 확인 가능)\n" +
                    "- `COMPLETED`: 완료\n" +
                    "- `FAILED`: 실패 (errorMessage에서 오류 내용 확인)\n\n" +
                    "**폴링 권장 주기:** 3~5초"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ImageGenerationJob.class))),
            @ApiResponse(responseCode = "404", description = "작업을 찾을 수 없음")
    })
    @GetMapping("/generate-images/status/{jobId}")
    public ResponseEntity<Map<String, Object>> getJobStatus(
            @Parameter(description = "작업 ID", required = true) 
            @PathVariable String jobId) {
        
        log.debug("작업 상태 조회: jobId={}", jobId);

        return contestImageService.getJobStatus(jobId)
                .map(job -> ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", Map.of(
                                "jobId", job.getJobId(),
                                "contestId", job.getContestId(),
                                "status", job.getStatus().name(),
                                "progress", job.getProgress(),
                                "totalRounds", job.getTotalRounds(),
                                "currentRound", job.getCurrentRound() != null ? job.getCurrentRound() : "",
                                "errorMessage", job.getErrorMessage() != null ? job.getErrorMessage() : "",
                                "results", job.getResults(),
                                "createdAt", job.getCreatedAt().toString(),
                                "updatedAt", job.getUpdatedAt().toString()
                        )
                )))
                .orElse(ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "message", "작업을 찾을 수 없습니다: " + jobId
                )));
    }

    // ==================== 동기 방식 (기존 호환용) ====================

    @Operation(
            summary = "대회 이미지 생성 (동기)", 
            description = "⚠️ **주의: 3~5분 소요, 504 타임아웃 위험**\n\n" +
                    "기존 방식 호환용. 비동기 방식(`POST /{contestId}/generate-images`) 사용 권장.\n\n" +
                    "특정 대회의 4개 라운드 1등 글로 AI 이미지를 생성하고 완료될 때까지 대기합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 생성 완료"),
            @ApiResponse(responseCode = "404", description = "대회를 찾을 수 없음"),
            @ApiResponse(responseCode = "504", description = "타임아웃 (Nginx/Spring 설정에 따라)")
    })
    @PostMapping("/{contestId}/generate-images-sync")
    public ResponseEntity<Map<String, Object>> generateContestImagesSync(
            @Parameter(description = "대회 ID", required = true) 
            @PathVariable Long contestId) {
        
        log.info("대회 이미지 생성 요청 (동기): contestId={}", contestId);
        
        List<ContestResult> results = contestImageService.generateContestImages(contestId);
        
        List<ContestResultResponse> responseList = results.stream()
                .map(ContestResultResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", results.size() + "개의 이미지가 생성되었습니다.",
                "data", responseList
        ));
    }
}
