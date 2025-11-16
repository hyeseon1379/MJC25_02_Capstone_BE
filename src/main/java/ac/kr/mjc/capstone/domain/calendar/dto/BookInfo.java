package ac.kr.mjc.capstone.domain.calendar.dto;
import ac.kr.mjc.capstone.domain.book.entity.Book;
import ac.kr.mjc.capstone.global.media.dto.ImageFileResponse;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "도서 정보 응답")
public class BookInfo {
    @Schema(description = "도서 ID", example = "1")
    private Long bookId;

    @Schema(description = "도서 제목", example = "토지")
    private String title;

    @Schema(description = "도서 저자", example = "박경리")
    private String author;

    @Schema(description = "도서 출판사", example = "마로니에북스")
    private String publisher;

    private ImageFileResponse image;

    public static BookInfo from(Book book) {

        ImageFileEntity imageEntity = book.getImage();
        ImageFileResponse imageResponse = ImageFileResponse.from(imageEntity);

        return BookInfo.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .image(imageResponse)
                .build();

    }
}
