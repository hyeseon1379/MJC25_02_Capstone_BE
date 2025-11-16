package ac.kr.mjc.capstone.domain.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Schema(description = "캘린더 상세 정보 응답")
public class CalendarDailyResponse {
    @Schema(description = "특정 날짜", example = "2026-01-01")
    private LocalDate date;
    private List<DailyRecordInfo> records;
}