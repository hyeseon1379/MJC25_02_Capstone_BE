package ac.kr.mjc.capstone.domain.book.dto;

import ac.kr.mjc.capstone.domain.book.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@Schema(description = "도서 정보 응답")
public class BookResponse {
    @Schema(description = "도서 ID", example = "1")
    private Long bookId;

    @Schema(description = "도서 제목", example = "토지")
    private String title;

    @Schema(description = "도서 저자", example = "박경리")
    private String author;

    @Schema(description = "도서 출판사", example = "마로니에북스")
    private String publisher;

    @Schema(description = "도서 표지 url", example = "https://example.com/images/toji-cover.jpg")
    private String imgUrl;

    private List<BookDetailsResponse> bookDetails;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .imgUrl(book.getImgUrl())
                .bookDetails(book.getBookDetails().stream()
                        .map(BookDetailsResponse::from).collect(Collectors.toList()))
                .build();
    }
}
