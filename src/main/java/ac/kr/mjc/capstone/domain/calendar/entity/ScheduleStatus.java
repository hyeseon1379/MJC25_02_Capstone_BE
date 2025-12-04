package ac.kr.mjc.capstone.domain.calendar.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "독서 일정 상태")
public enum ScheduleStatus {
    @Schema(description = "읽을 예정")
    TO_READ,

    @Schema(description = "읽는 중")
    READING,

    @Schema(description = "완료")
    COMPLETED
}
