package ac.kr.mjc.capstone.domain.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "도서 일정 전체 삭제 응답")
public class BookScheduleDeleteResponse {

    @Schema(description = "도서 ID", example = "123")
    private Long bookId;

    @Schema(description = "삭제된 일정 수", example = "2")
    private Long deletedCount;

    public static BookScheduleDeleteResponse of(Long bookId, Long deletedCount) {
        return BookScheduleDeleteResponse.builder()
                .bookId(bookId)
                .deletedCount(deletedCount)
                .build();
    }
}
