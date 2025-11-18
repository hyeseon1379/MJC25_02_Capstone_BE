package ac.kr.mjc.capstone.domain.contest.service.inf;

import ac.kr.mjc.capstone.domain.contest.dto.ContestDetailsRequest;
import ac.kr.mjc.capstone.domain.contest.dto.ContestDetailsResponse;
import ac.kr.mjc.capstone.domain.contest.dto.ContestRequest;
import ac.kr.mjc.capstone.domain.contest.dto.ContestResponse;
import ac.kr.mjc.capstone.global.response.ApiResponse;

import java.util.List;

public interface ContestService {
    ApiResponse<ContestResponse> createContest(Long userId, ContestRequest contestRequest);
    ApiResponse<ContestResponse> getContest(Long contestId);
    ApiResponse<List<ContestResponse>> getAllContest();
    ApiResponse<ContestDetailsResponse> createContestDetails(ContestDetailsRequest contestDetailsRequest);
}
