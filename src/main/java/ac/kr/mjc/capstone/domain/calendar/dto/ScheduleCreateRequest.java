package ac.kr.mjc.capstone.domain.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "일정 등록 요청")
public class ScheduleCreateRequest {

    @NotNull(message = "도서 ID는 필수입니다.")
    @Schema(description = "도서 ID", example = "123", required = true)
    private Long bookId;

    @NotEmpty(message = "일정 목록은 비어있을 수 없습니다.")
    @Valid
    @Schema(description = "일정 목록")
    private List<ScheduleItem> schedules;

    @Data
    @Schema(description = "개별 일정 항목")
    public static class ScheduleItem {

        @Schema(description = "자녀 ID (본인이면 null)", example = "5")
        private Long childId;

        @NotNull(message = "시작일은 필수입니다.")
        @Schema(description = "시작일", example = "2025-01-15", required = true)
        private LocalDate startDate;

        @Schema(description = "완료일 (선택)", example = "2025-01-30")
        private LocalDate endDate;
    }
}
