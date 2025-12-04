package ac.kr.mjc.capstone.domain.calendar.dto;

import ac.kr.mjc.capstone.domain.calendar.entity.CalendarSchedule;
import ac.kr.mjc.capstone.domain.calendar.entity.ScheduleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@Schema(description = "도서별 일정 조회 응답")
public class BookSchedulesResponse {

    @Schema(description = "도서 정보")
    private ScheduleBookInfo book;

    @Schema(description = "일정 목록")
    private List<ScheduleItem> schedules;

    @Data
    @Builder
    @Schema(description = "일정 항목")
    public static class ScheduleItem {

        @Schema(description = "일정 ID", example = "1001")
        private Long scheduleId;

        @Schema(description = "독자 정보")
        private ScheduleReaderInfo reader;

        @Schema(description = "시작일", example = "2025-01-15")
        private LocalDate startDate;

        @Schema(description = "완료일", example = "2025-01-30")
        private LocalDate endDate;

        @Schema(description = "상태", example = "READING")
        private ScheduleStatus status;

        public static ScheduleItem from(CalendarSchedule schedule) {
            return ScheduleItem.builder()
                    .scheduleId(schedule.getScheduleId())
                    .reader(ScheduleReaderInfo.from(schedule))
                    .startDate(schedule.getStartDate())
                    .endDate(schedule.getEndDate())
                    .status(schedule.calculateStatus())
                    .build();
        }
    }

    public static BookSchedulesResponse from(List<CalendarSchedule> schedules) {
        if (schedules.isEmpty()) {
            return null;
        }

        CalendarSchedule first = schedules.get(0);
        return BookSchedulesResponse.builder()
                .book(ScheduleBookInfo.from(first.getBook()))
                .schedules(schedules.stream()
                        .map(ScheduleItem::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
