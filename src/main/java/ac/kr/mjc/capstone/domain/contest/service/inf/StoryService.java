package ac.kr.mjc.capstone.domain.contest.service.inf;

import ac.kr.mjc.capstone.domain.contest.dto.*;
import ac.kr.mjc.capstone.global.response.ApiResponse;

import java.util.List;

public interface StoryService {
    ApiResponse<StoryResponse> createStory(Long userId, Long contestDetailsId, StoryRequest storyRequest);
    ApiResponse<List<StoryResponse>> getStoryList(Long contestDetailsId);
    ApiResponse<StoryResponse> updateStory(Long userId, Long contestDetailsId, Long storyId, StoryRequest contestRequest);
    ApiResponse<Void> deleteStory(Long userId, Long contestDetailsId, Long storyId);

    ApiResponse<Void> createVote(Long userId, Long contestDetailsId, Long storyId);
    ApiResponse<List<StoryVoteResponse>> getStoryListVoted(Long contestDetailsId);
}
