package ac.kr.mjc.capstone.domain.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "일정 수정 요청")
public class ScheduleUpdateRequest {

    @Schema(description = "자녀 ID (독자 변경 시, 본인이면 null)", example = "5")
    private Long childId;

    @NotNull(message = "시작일은 필수입니다.")
    @Schema(description = "시작일", example = "2025-01-18", required = true)
    private LocalDate startDate;

    @Schema(description = "완료일", example = "2025-02-10")
    private LocalDate endDate;
}
