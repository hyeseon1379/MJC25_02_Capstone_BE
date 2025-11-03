package ac.kr.mjc.capstone.domain.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Getter
@NoArgsConstructor
@Schema(description = "도서 상세 정보 생성 요청")
public class BookDetailsRequest {
    @Schema(description = "자녀 ID", example = "1")
    private Long childId;

    @Schema(description = "독서 시작일", example = "2025-11-03")
    private LocalDate startDate;

    @Schema(description = "독서 종료일", example = "2025-11-10")
    private LocalDate endDate;
}
