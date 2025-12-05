package ac.kr.mjc.capstone.domain.calendar.dto;

import ac.kr.mjc.capstone.domain.calendar.entity.CalendarSchedule;
import ac.kr.mjc.capstone.domain.calendar.entity.ScheduleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Schema(description = "일정 등록 응답")
public class ScheduleCreateResponse {

    @Schema(description = "도서 ID", example = "123")
    private Long bookId;

    @Schema(description = "등록된 일정 목록")
    private List<ScheduleItem> schedules;

    @Data
    @Builder
    @Schema(description = "등록된 개별 일정")
    public static class ScheduleItem {

        @Schema(description = "일정 ID", example = "1001")
        private Long scheduleId;

        @Schema(description = "자녀 ID (본인이면 null)", example = "5")
        private Long childId;

        @Schema(description = "독자 이름", example = "홍길순")
        private String readerName;

        @Schema(description = "독자 색상", example = "#FF6B6B")
        private String readerColor;

        @Schema(description = "시작일", example = "2025-01-15")
        private LocalDate startDate;

        @Schema(description = "완료일", example = "2025-01-30")
        private LocalDate endDate;

        @Schema(description = "상태", example = "READING")
        private ScheduleStatus status;

        public static ScheduleItem from(CalendarSchedule schedule) {
            return ScheduleItem.builder()
                    .scheduleId(schedule.getScheduleId())
                    .childId(schedule.getChild() != null ? schedule.getChild().getChildId() : null)
                    .readerName(schedule.getReaderName())
                    .readerColor(schedule.getReaderColor())
                    .startDate(schedule.getStartDate())
                    .endDate(schedule.getEndDate())
                    .status(schedule.calculateStatus())
                    .build();
        }
    }
}
