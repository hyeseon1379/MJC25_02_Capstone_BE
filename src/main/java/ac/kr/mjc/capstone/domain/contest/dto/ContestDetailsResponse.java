package ac.kr.mjc.capstone.domain.contest.dto;

import ac.kr.mjc.capstone.domain.contest.entity.ContestDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@Schema(description = "대회 상세 정보 응답")
public class ContestDetailsResponse {
    @Schema(description = "대회 ID", example = "1")
    private Long contestId;

    @Schema(description = "대회 상세 ID", example = "1")
    private Long contestDetailsId;

    @Schema(description = "진행 상태", example = "진행 중")
    private String progressStatus;

    @Schema(description = "라운드", example = "1차")
    private String round;

    @Schema(description = "시작 문구", example = "")
    private String startPrompt;

    @Schema(description = "대회 시작일", example = "2025-11-03")
    private LocalDate startDate;

    @Schema(description = "대회 종료일", example = "2025-11-10")
    private LocalDate endDate;

    public static ContestDetailsResponse from(ContestDetails contestDetails) {
        return ContestDetailsResponse.builder()
                .contestId(contestDetails.getContest().getContestId())
                .contestDetailsId(contestDetails.getDetailsId())
                .round(contestDetails.getRound().getDisplayName())
                .progressStatus(contestDetails.getProgressStatus().getDisplayName())
                .startPrompt(contestDetails.getStartPrompt())
                .startDate(contestDetails.getStartDate())
                .endDate(contestDetails.getEndDate())
                .build();
    }
}
