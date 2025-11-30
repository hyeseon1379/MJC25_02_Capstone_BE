package ac.kr.mjc.capstone.domain.contest.controller;

import ac.kr.mjc.capstone.domain.contest.dto.ContestDetailsResponse;
import ac.kr.mjc.capstone.domain.contest.dto.ContestDetailsUpdateRequest;
import ac.kr.mjc.capstone.domain.contest.dto.StoryRequest;
import ac.kr.mjc.capstone.domain.contest.dto.StoryResponse;
import ac.kr.mjc.capstone.domain.contest.service.inf.StoryService;
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
@RequestMapping("/api/story")
@RequiredArgsConstructor
@Tag(name = "Story", description = "창작놀이_이어쓰기 API")
public class StoryController {

    private final StoryService storyService;

    @PostMapping("/{contestDetailsId}")
    @Operation(summary = "이어쓰기 정보 생성", description = "이어쓰기 정보를 생성합니다")
    public ResponseEntity<ApiResponse<StoryResponse>> createStory(@AuthenticationPrincipal Long userId,
                                                                  @PathVariable("contestDetailsId") Long contestDetailsId,
                                                                      @Valid @RequestBody StoryRequest request){
        ApiResponse<StoryResponse> storyResponse = storyService.createStory(userId, contestDetailsId, request);
        return ResponseEntity.status(201).body(storyResponse);
    }

    @GetMapping("/{contestDetailsId}")
    @Operation(summary = "이어쓰기 목록 조회", description = "이어쓰기 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<StoryResponse>>> getStoryList(@PathVariable("contestDetailsId") Long contestDetailsId) {
        ApiResponse<List<StoryResponse>> response = storyService.getStoryList(contestDetailsId);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{contestDetailsId}/{storyId}")
    @Operation(summary = "이어쓰기 정보 수정", description = "이어쓰기 정보를 수정합니다")
    public ResponseEntity<ApiResponse<StoryResponse>> updateStory(@AuthenticationPrincipal Long userId,
                                                                                    @PathVariable("contestDetailsId") Long contestDetailsId,
                                                                                    @PathVariable("storyId") Long storyId,
                                                                                    @Valid @RequestBody StoryRequest request){

        ApiResponse<StoryResponse> response = storyService.updateStory(userId, contestDetailsId, storyId, request);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{contestDetailsId}/{storyId}")
    @Operation(summary = "이어쓰기 삭제", description = "이어쓰기를 삭제합니다")
    public ApiResponse<Void> deleteStory(@AuthenticationPrincipal Long userId,
                                                  @PathVariable("contestDetailsId") Long contestDetailsId,
                                                  @PathVariable("storyId") Long storyId) {

        storyService.deleteStory(userId, contestDetailsId, storyId);

        return ApiResponse.success("이어쓰기 삭제 성공");
    }
}
