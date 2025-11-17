package ac.kr.mjc.capstone.domain.contest.service.impl;

import ac.kr.mjc.capstone.domain.contest.dto.ContestRequest;
import ac.kr.mjc.capstone.domain.contest.dto.ContestResponse;
import ac.kr.mjc.capstone.domain.contest.entity.Contest;
import ac.kr.mjc.capstone.domain.contest.entity.ProgressStatus;
import ac.kr.mjc.capstone.domain.contest.repository.ContestRepository;
import ac.kr.mjc.capstone.domain.contest.service.inf.ContestService;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import ac.kr.mjc.capstone.global.media.repository.FileRepository;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContestServiceImpl implements ContestService {
    private final ContestRepository contestRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Override
    public ApiResponse<ContestResponse> createContest(Long userId, ContestRequest contestRequest){
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ImageFileEntity image = fileRepository.findById(contestRequest.getImageId())
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        Contest contest = Contest.builder()
                .title(contestRequest.getTitle())
                .content(contestRequest.getContent())
                .startDate(contestRequest.getStartDate())
                .endDate(contestRequest.getEndDate())
                .progressStatus(calculateProgressStatus(contestRequest.getStartDate(), contestRequest.getEndDate()))
                .image(image)
                .user(userEntity)
                .build();

        Contest savedContest = contestRepository.save(contest);
        ContestResponse response = ContestResponse.from(savedContest);
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<List<ContestResponse>> getAllContest(){
        List<Contest> contests = contestRepository.findAllByOrderByStartDateDesc();

        List<ContestResponse> responses = contests.stream().map(ContestResponse::from).toList();

        return ApiResponse.success(responses);
    }

    @Override
    public ApiResponse<ContestResponse> getContest(Long contestId){
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTEST_NOT_FOUND));

        ContestResponse contestResponse = ContestResponse.from(contest);
        return ApiResponse.success(contestResponse);
    }

    private ProgressStatus calculateProgressStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) {
            return ProgressStatus.PLANNED;
        } else if (!today.isAfter(endDate)) {
            return ProgressStatus.ONGOING;
        } else {
            return ProgressStatus.CANCELLED;
        }
    }

    private ProgressStatus calculateProgressStatus_Details(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) {
            return ProgressStatus.PLANNED;
        }
        LocalDate writingEndDay = startDate.plusDays(9); //이어쓰기 종료

        LocalDate votingEndDay = startDate.plusDays(12); //투표 종료

        if (today.isAfter(votingEndDay) && !today.isAfter(endDate)) {
            return ProgressStatus.CANCELLED;
        }

//        // 6. 투표 단계 (이어쓰기 종료일 다음 날부터 투표 종료일까지)
//        else if (today.isAfter(writingEndDay) && !today.isAfter(votingEndDay)) {
//            // 이어쓰기가 끝났고, 아직 투표가 끝나지 않았으면
//            return ProgressStatus.COMPLETED; // COMPLETED를 투표(VOTING)로 사용한다고 가정
//        }

        else if (!today.isAfter(writingEndDay)) {
            return ProgressStatus.ONGOING;
        }

        else if (today.isAfter(endDate)) {
            return ProgressStatus.COMPLETED;
        }

        return ProgressStatus.ONGOING;
    }
}
