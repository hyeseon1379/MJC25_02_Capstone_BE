package ac.kr.mjc.capstone.domain.contest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "대회 리스트 삭제 요청")
public class ContestDeleteRequest {
    @Schema(description = "대회 IDs", example = "1, 2")
    private List<Long> contestIds;
}
