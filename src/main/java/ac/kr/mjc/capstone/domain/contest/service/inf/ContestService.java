package ac.kr.mjc.capstone.domain.contest.service.inf;

import ac.kr.mjc.capstone.domain.contest.dto.*;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ContestService {
    ApiResponse<ContestResponse> createContest(Long userId, ContestRequest contestRequest);
    ApiResponse<ContestResponse> getContest(Long contestId);
    ApiResponse<List<ContestResponse>> getAllContest();
    ApiResponse<ContestResponse> updateContest(Long userId, Long contentId, ContestRequest contestRequest);
    ApiResponse<ContestDetailsResponse> createContestDetails(ContestDetailsRequest contestDetailsRequest);
    ApiResponse<List<ContestDetailsResponse>> getAllContestDetails(Long contestId);
    ApiResponse<ContestDetailsResponse> getContestDetails(Long contestId, Long contestDetailsId);
    ApiResponse<ContestDetailsResponse> updateContestDetails(Long userId, Long contentId, Long contestDetailsId , ContestDetailsUpdateRequest contestRequest);
}
