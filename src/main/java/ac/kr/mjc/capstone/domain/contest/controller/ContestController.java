package ac.kr.mjc.capstone.domain.contest.controller;

import ac.kr.mjc.capstone.domain.book.dto.BookResponse;
import ac.kr.mjc.capstone.domain.book.dto.BookUpdateRequest;
import ac.kr.mjc.capstone.domain.contest.dto.*;
import ac.kr.mjc.capstone.domain.contest.service.inf.ContestService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/contest")
@RequiredArgsConstructor
@Tag(name = "Contest", description = "창작놀이 API")
public class ContestController {

    private final ContestService contestService;

    @PostMapping
    @Operation(summary = "대회 정보 생성", description = "대회 정보를 생성합니다")
    public ResponseEntity<ApiResponse<ContestResponse>> createContest(@AuthenticationPrincipal Long userId,
                                                   @Valid @RequestBody ContestRequest request){
        ApiResponse<ContestResponse> contestResponse = contestService.createContest(userId, request);
        return ResponseEntity.status(201).body(contestResponse);
    }

    @GetMapping
    @Operation(summary = "대회 목록 조회", description = "대회 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<ContestResponse>>> getAllContest() {
        ApiResponse<List<ContestResponse>> contestResponse = contestService.getAllContest();
        return ResponseEntity.status(200).body(contestResponse);
    }

    @GetMapping("/{contestId}")
    @Operation(summary = "대회 정보 조회", description = "대회 정보를 조회합니다")
    public ResponseEntity<ApiResponse<ContestResponse>> getContest(@PathVariable("contestId") Long contestId) {
        ApiResponse<ContestResponse> contestResponse = contestService.getContest(contestId);
        return ResponseEntity.status(200).body(contestResponse);
    }

    @PutMapping("/{contestId}")
    @Operation(summary = "대회 정보 수정", description = "대회 정보를 수정합니다")
    public ResponseEntity<ApiResponse<ContestResponse>> updateContest(@AuthenticationPrincipal Long userId,
                                                @PathVariable("contestId") Long contestId,
                                                @Valid @RequestBody ContestRequest request){

        ApiResponse<ContestResponse> contestResponse = contestService.updateContest(userId, contestId, request);
        return ResponseEntity.status(200).body(contestResponse);
    }

    @PostMapping("/detail")
    @Operation(summary = "대회 상세 정보 생성", description = "대회 상세 정보를 생성합니다")
    public ResponseEntity<ApiResponse<ContestDetailsResponse>> createContestDetails(@Valid @RequestBody ContestDetailsRequest request){
        ApiResponse<ContestDetailsResponse> response = contestService.createContestDetails(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/detail/{contestId}")
    @Operation(summary = "대회 상세 목록 조회", description = "대회 상세 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<ContestDetailsResponse>>> getAllContestDetails(@PathVariable("contestId") Long contestId) {
        ApiResponse<List<ContestDetailsResponse>> response = contestService.getAllContestDetails(contestId);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/detail/{contestId}/{contestDetailsId}")
    @Operation(summary = "대회 상세 정보 조회", description = "대회 상세 정보를 조회합니다")
    public ResponseEntity<ApiResponse<ContestDetailsResponse>> getContestDetails(@PathVariable("contestId") Long contestId,
                                                                                 @PathVariable("contestDetailsId") Long contestDetailsId) {
        ApiResponse<ContestDetailsResponse> response = contestService.getContestDetails(contestId, contestDetailsId);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/detail/{contestId}/{contestDetailsId}")
    @Operation(summary = "대회 상세 정보 수정", description = "대회 상세 정보를 수정합니다")
    public ResponseEntity<ApiResponse<ContestDetailsResponse>> updateContestDetails(@AuthenticationPrincipal Long userId,
                                                                      @PathVariable("contestId") Long contestId,
                                                                      @PathVariable("contestDetailsId") Long contestDetailsId,
                                                                      @Valid @RequestBody ContestDetailsUpdateRequest request){

        ApiResponse<ContestDetailsResponse> response = contestService.updateContestDetails(userId, contestId, contestDetailsId, request);
        return ResponseEntity.status(200).body(response);
    }
}
