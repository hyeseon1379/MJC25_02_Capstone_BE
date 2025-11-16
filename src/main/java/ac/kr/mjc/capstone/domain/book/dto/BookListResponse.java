package ac.kr.mjc.capstone.domain.book.dto;

import ac.kr.mjc.capstone.domain.book.entity.Book;
import ac.kr.mjc.capstone.global.media.dto.ImageFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "도서 목록 응답")
public class BookListResponse {
    @Schema(description = "도서 ID", example = "1")
    private Long bookId;

    @Schema(description = "도서 제목", example = "토지")
    private String title;

    @Schema(description = "도서 저자", example = "박경리")
    private String author;

    @Schema(description = "도서 출판사", example = "마로니에북스")
    private String publisher;

    private ImageFileResponse image;

    public static BookListResponse from(Book book) {
        return BookListResponse.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .image(ImageFileResponse.from(book.getImage()))
                .build();
    }
}
