package ac.kr.mjc.capstone.domain.calendar.dto;

import ac.kr.mjc.capstone.domain.calendar.entity.CalendarSchedule;
import ac.kr.mjc.capstone.domain.calendar.entity.ScheduleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@Schema(description = "일정 응답")
public class ScheduleResponse {

    @Schema(description = "일정 ID", example = "1001")
    private Long scheduleId;

    @Schema(description = "도서 정보")
    private ScheduleBookInfo book;

    @Schema(description = "독자 정보")
    private ScheduleReaderInfo reader;

    @Schema(description = "시작일", example = "2025-01-15")
    private LocalDate startDate;

    @Schema(description = "완료일", example = "2025-01-30")
    private LocalDate endDate;

    @Schema(description = "상태", example = "READING")
    private ScheduleStatus status;

    public static ScheduleResponse from(CalendarSchedule schedule) {
        return ScheduleResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .book(ScheduleBookInfo.from(schedule.getBook()))
                .reader(ScheduleReaderInfo.from(schedule))
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .status(schedule.calculateStatus())
                .build();
    }
}
