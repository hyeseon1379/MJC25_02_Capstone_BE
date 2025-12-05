package ac.kr.mjc.capstone.domain.contest.dto;

import ac.kr.mjc.capstone.domain.contest.entity.ContestResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestResultResponse {
    private Long resultId;
    private Long contestId;
    private String title;
    private String finalContent;
    private String imagePath;

    public static ContestResultResponse from(ContestResult result) {
        return ContestResultResponse.builder()
                .resultId(result.getResultId())
                .contestId(result.getContest().getContestId())
                .title(result.getTitle())
                .finalContent(result.getFinalContent())
                .imagePath(result.getImage() != null ? result.getImage().getFilePath() : null)
                .build();
    }
}
