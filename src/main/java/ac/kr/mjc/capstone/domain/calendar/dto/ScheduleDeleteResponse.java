package ac.kr.mjc.capstone.domain.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "일정 삭제 응답")
public class ScheduleDeleteResponse {

    @Schema(description = "삭제된 일정 ID", example = "1002")
    private Long deletedScheduleId;

    public static ScheduleDeleteResponse of(Long scheduleId) {
        return ScheduleDeleteResponse.builder()
                .deletedScheduleId(scheduleId)
                .build();
    }
}
