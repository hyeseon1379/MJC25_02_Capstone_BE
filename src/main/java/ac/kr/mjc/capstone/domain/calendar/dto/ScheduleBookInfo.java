package ac.kr.mjc.capstone.domain.calendar.dto;

import ac.kr.mjc.capstone.domain.book.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "일정 도서 정보")
public class ScheduleBookInfo {

    @Schema(description = "도서 ID", example = "123")
    private Long bookId;

    @Schema(description = "도서 제목", example = "어린왕자")
    private String title;

    @Schema(description = "저자", example = "생텍쥐페리")
    private String author;

    @Schema(description = "표지 URL", example = "https://...")
    private String coverUrl;

    @Schema(description = "이미지 ID", example = "456")
    private Long imageId;

    public static ScheduleBookInfo from(Book book) {
        return ScheduleBookInfo.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .coverUrl(book.getCoverUrl())
                .imageId(book.getImage() != null ? book.getImage().getImageId() : null)
                .build();
    }
}
