package ac.kr.mjc.capstone.domain.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@Schema(description = "캘린더 도서 상세 정보 응답")
public class DailyRecordInfo {
    @Schema(description = "도서 상세 ID", example = "1")
    private Long detailsId;

    private ReaderInfo reader;

    private BookInfo book;

    @Schema(description = "독서 시작일", example = "2025-11-03")
    private LocalDate startDate;

    @Schema(description = "독서 종료일", example = "2025-11-10")
    private LocalDate endDate;
}