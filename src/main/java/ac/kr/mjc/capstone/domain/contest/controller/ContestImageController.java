package ac.kr.mjc.capstone.domain.contest.controller;

import ac.kr.mjc.capstone.domain.contest.dto.ContestResultResponse;
import ac.kr.mjc.capstone.domain.contest.entity.ContestResult;
import ac.kr.mjc.capstone.domain.contest.service.impl.ContestImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Contest Image", description = "대회 AI 이미지 생성 API")
@RestController
@RequestMapping("/api/admin/contest")
@RequiredArgsConstructor
public class ContestImageController {

    private final ContestImageService contestImageService;

    @Operation(summary = "대회 이미지 생성", description = "특정 대회의 4개 라운드 1등 글로 AI 이미지 생성")
    @PostMapping("/{contestId}/generate-images")
    public ResponseEntity<Map<String, Object>> generateContestImages(@PathVariable Long contestId) {
        log.info("대회 이미지 생성 요청: contestId={}", contestId);
        
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
