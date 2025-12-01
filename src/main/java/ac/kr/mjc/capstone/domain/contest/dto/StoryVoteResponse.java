package ac.kr.mjc.capstone.domain.contest.dto;

import ac.kr.mjc.capstone.domain.contest.entity.Story;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "이어쓰기 정보 응답")
public class StoryVoteResponse {
    @Schema(description = "이어쓰기 ID", example = "1")
    private Long storyId;

    @Schema(description = "대회 상세 ID", example = "1")
    private Long detailId;

    @Schema(description = "작성자 ID", example = "1")
    private Long userId;

    @Schema(description = "이어쓰기 설명", example = "")
    private String content;

    @Schema(description = "이어쓰기 투표수", example = "1")
    private int voteCount;

    public static StoryVoteResponse from(Story story) {
        return StoryVoteResponse.builder()
                .storyId(story.getStoryId())
                .detailId(story.getContestDetails().getDetailsId())
                .userId(story.getUser().getUserId())
                .content(story.getContent())
                .voteCount(story.getVoteCount())
                .build();
    }

}
