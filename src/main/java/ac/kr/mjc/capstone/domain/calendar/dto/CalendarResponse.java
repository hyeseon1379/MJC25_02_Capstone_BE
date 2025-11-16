package ac.kr.mjc.capstone.domain.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "캘린더 정보 응답")
public class CalendarResponse {
    @Schema(description = "날짜", example = "1")
    private int day;
    private List<ReaderInfo> readers;
}