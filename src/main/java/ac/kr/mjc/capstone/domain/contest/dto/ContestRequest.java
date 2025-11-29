package ac.kr.mjc.capstone.domain.contest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "대회 생성 요청")
public class ContestRequest {
    @NotBlank(message = "대회 제목을 입력해주세요")
    @Size(max = 255, message = "대회 제목은 최대 255자까지 가능합니다")
    @Schema(description = "대회 제목", example = "")
    private String title;

    @Schema(description = "대회 설명", example = "")
    private String content;

    @Schema(description = "대회 시작일", example = "2025-11-03")
    private LocalDate startDate;

    @Schema(description = "대회 종료일", example = "2025-11-10")
    private LocalDate endDate;

    @Schema(description = "대회 표지 ID", example = "1")
    private Long imageId;
}
