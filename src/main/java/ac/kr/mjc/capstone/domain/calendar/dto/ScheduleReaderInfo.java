package ac.kr.mjc.capstone.domain.calendar.dto;

import ac.kr.mjc.capstone.domain.calendar.entity.CalendarSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "일정 독자 정보")
public class ScheduleReaderInfo {

    @Schema(description = "자녀 ID (본인이면 null)", example = "5")
    private Long childId;

    @Schema(description = "독자 이름", example = "홍길순")
    private String readerName;

    @Schema(description = "독자 색상", example = "#FF6B6B")
    private String color;

    public static ScheduleReaderInfo from(CalendarSchedule schedule) {
        return ScheduleReaderInfo.builder()
                .childId(schedule.getChild() != null ? schedule.getChild().getChildId() : null)
                .readerName(schedule.getReaderName())
                .color(schedule.getReaderColor())
                .build();
    }
}
