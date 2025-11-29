package ac.kr.mjc.capstone.domain.contest.service.inf;

import ac.kr.mjc.capstone.domain.contest.dto.*;
import ac.kr.mjc.capstone.global.response.ApiResponse;

import java.util.List;

public interface ContestService {
    ApiResponse<ContestResponse> createContest(Long userId, ContestRequest contestRequest);
    ApiResponse<ContestResponse> getContest(Long contestId);
    ApiResponse<List<ContestResponse>> getAllContest();
    ApiResponse<ContestResponse> updateContest(Long userId, Long contentId, ContestRequest contestRequest);
    ApiResponse<Void> deleteContest(Long userId, Long contestId);
    ApiResponse<Void> deleteContests(Long userId, ContestDeleteRequest contestDeleteRequest);


    ApiResponse<ContestDetailsResponse> createContestDetails(ContestDetailsRequest contestDetailsRequest);
    ApiResponse<List<ContestDetailsResponse>> getAllContestDetails(Long contestId);
    ApiResponse<ContestDetailsResponse> getContestDetails(Long contestId, Long contestDetailsId);
    ApiResponse<ContestDetailsResponse> updateContestDetails(Long userId, Long contentId, Long contestDetailsId , ContestDetailsUpdateRequest contestRequest);
    ApiResponse<Void> deleteContestDetails(Long userId, Long contestId, Long contestDetailsId);

}
