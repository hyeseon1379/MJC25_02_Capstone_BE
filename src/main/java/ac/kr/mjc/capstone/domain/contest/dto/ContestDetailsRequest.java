package ac.kr.mjc.capstone.domain.contest.dto;

import ac.kr.mjc.capstone.domain.contest.entity.Round;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "대회 상세 정보 생성 요청")
public class ContestDetailsRequest {
    @Schema(description = "대회 제목", example = "")
    private String startPrompt;

    @Schema(description = "대회 설명", example = "")
    private String content;

    @Schema(description = "대회 라운드", example = "ROUND_1")
    private Round round;

    @Schema(description = "대회 시작일", example = "2025-11-03")
    private LocalDate startDate;

    @Schema(description = "대회 종료일", example = "2025-11-10")
    private LocalDate endDate;
}
