package ac.kr.mjc.capstone.domain.contest.service.impl;

import ac.kr.mjc.capstone.domain.contest.dto.*;
import ac.kr.mjc.capstone.domain.contest.entity.Contest;
import ac.kr.mjc.capstone.domain.contest.entity.ContestDetails;
import ac.kr.mjc.capstone.domain.contest.entity.Story;
import ac.kr.mjc.capstone.domain.contest.entity.Vote;
import ac.kr.mjc.capstone.domain.contest.repository.ContestDetailsRepository;
import ac.kr.mjc.capstone.domain.contest.repository.StoryRepository;
import ac.kr.mjc.capstone.domain.contest.repository.VoteRepository;
import ac.kr.mjc.capstone.domain.contest.service.inf.StoryService;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StoryServiceImpl implements StoryService {
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final ContestDetailsRepository contestDetailsRepository;
    private final VoteRepository voteRepository;

    @Override
    public ApiResponse<StoryResponse> createStory(Long userId, Long contestDetailsId, StoryRequest storyRequest){
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ContestDetails details = contestDetailsRepository.findById(contestDetailsId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTEST_DETAILS_NOT_FOUND));

        Story story = Story.builder()
                .contestDetails(details)
                .user(userEntity)
                .content(storyRequest.getContent())
                .build();

        Story savedStory = storyRepository.save(story);
        StoryResponse response = StoryResponse.from(savedStory);
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<List<StoryResponse>> getStoryList(Long contestDetailsId){
        ContestDetails details = contestDetailsRepository.findById(contestDetailsId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTEST_DETAILS_NOT_FOUND));

        List<Story> stories = storyRepository.findByContestDetailsOrderByCreateAtDesc(details);

        List<StoryResponse> responses = stories.stream().map(StoryResponse::from).toList();

        return ApiResponse.success(responses);
    }

    @Override
    public ApiResponse<StoryResponse> updateStory(Long userId, Long contestDetailsId, Long storyId, StoryRequest request){

        ContestDetails details = contestDetailsRepository.findById(contestDetailsId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTEST_DETAILS_NOT_FOUND));

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORY_NOT_FOUND));

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!story.getUser().equals(userEntity)){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        story.update(
                request.getContent() != null ? request.getContent() : story.getContent()
        );

        StoryResponse response = StoryResponse.from(story);
        return ApiResponse.success(response);
    }


    @Override
    public ApiResponse<Void> deleteStory(Long userId, Long contestDetailsId, Long storyId){

        ContestDetails details = contestDetailsRepository.findById(contestDetailsId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTEST_DETAILS_NOT_FOUND));

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORY_NOT_FOUND));

        if(!story.getUser().getUserId().equals(userId)){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        storyRepository.delete(story);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<Void> createVote(Long userId, Long contestDetailsId, Long storyId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ContestDetails details = contestDetailsRepository.findById(contestDetailsId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTEST_DETAILS_NOT_FOUND));

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORY_NOT_FOUND));

        //자신 투표 방지
        if(story.getUser().equals(userEntity)){
            throw new CustomException(ErrorCode.ALREADY_EXISTS);
        }

        // 중복 투표 방지
        if (voteRepository.existsByStoryAndUser(story, userEntity)) {
            throw new CustomException(ErrorCode.ALREADY_VOTED);
        }

        if (voteRepository.existsByContestDetailsAndUser(details, userEntity)){
            throw new CustomException(ErrorCode.ALREADY_VOTED);
        }

        Vote vote = Vote.builder()
                .story(story)
                .user(userEntity)
                .contestDetails(details)
                .votedAt(LocalDateTime.now())
                .build();

        int voteCount = story.getVoteCount();
        voteCount += 1;
        story.setVoteCount(voteCount);
        storyRepository.save(story);
        voteRepository.save(vote);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<List<StoryVoteResponse>> getStoryListVoted(Long contestDetailsId){
        ContestDetails details = contestDetailsRepository.findById(contestDetailsId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTEST_DETAILS_NOT_FOUND));

        List<Story> stories = storyRepository.findByContestDetailsOrderByCreateAtDesc(details);

        List<StoryVoteResponse> responses = stories.stream().map(StoryVoteResponse::from).toList();

        return ApiResponse.success(responses);
    }
}
