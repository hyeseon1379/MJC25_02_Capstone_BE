package ac.kr.mjc.capstone.domain.contest.dto;

import ac.kr.mjc.capstone.domain.contest.entity.Contest;
import ac.kr.mjc.capstone.global.media.dto.ImageFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@Schema(description = "대회 정보 응답")
public class ContestResponse {
    @Schema(description = "대회 ID", example = "1")
    private Long contestId;

    @Schema(description = "진행 상태", example = "진행 중")
    private String progressStatus;

    @Schema(description = "대회 제목", example = "")
    private String title;

    @Schema(description = "대회 설명", example = "")
    private String content;

    @Schema(description = "대회 시작일", example = "2025-11-03")
    private LocalDate startDate;

    @Schema(description = "대회 종료일", example = "2025-11-10")
    private LocalDate endDate;

    private ImageFileResponse image;

    public static ContestResponse from(Contest contest) {
        return ContestResponse.builder()
                .contestId(contest.getContestId())
                .progressStatus(contest.getProgressStatus().getDisplayName())
                .title(contest.getTitle())
                .content(contest.getContent())
                .startDate(contest.getStartDate())
                .endDate(contest.getEndDate())
                .image(ImageFileResponse.from(contest.getImage()))
                .build();
    }
}
