package ac.kr.mjc.capstone.domain.contest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "이어쓰기 생성 요청")
public class StoryRequest {
    @Schema(description = "이어쓰기 설명", example = "")
    private String content;
}
