package ac.kr.mjc.capstone.domain.contest.dto;

import ac.kr.mjc.capstone.domain.contest.entity.Contest;
import ac.kr.mjc.capstone.domain.contest.entity.Story;
import ac.kr.mjc.capstone.global.media.dto.ImageFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "이어쓰기 정보 응답")
public class StoryResponse {
    @Schema(description = "이어쓰기 ID", example = "1")
    private Long storyId;

    @Schema(description = "대회 상세 ID", example = "1")
    private Long detailId;

    @Schema(description = "작성자 ID", example = "1")
    private Long userId;

    @Schema(description = "이어쓰기 설명", example = "")
    private String content;

    public static StoryResponse from(Story story) {
        return StoryResponse.builder()
                .storyId(story.getStoryId())
                .detailId(story.getContestDetails().getDetailsId())
                .userId(story.getUser().getUserId())
                .content(story.getContent())
                .build();
    }

}
