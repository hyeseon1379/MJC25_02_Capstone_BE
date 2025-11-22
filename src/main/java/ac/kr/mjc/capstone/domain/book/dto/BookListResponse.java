package ac.kr.mjc.capstone.domain.book.dto;

import ac.kr.mjc.capstone.domain.book.entity.Book;
import ac.kr.mjc.capstone.global.media.dto.ImageFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
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

    @Schema(description = "isbn번호", example = "9783929979701")
    private String isbn;

    @Schema(description = "출판년도", example = " 1994")
    private String publicationYear;

    @Schema(description = "표지 이미지 URL", example = "https://....")
    private String coverUrl;

    @Schema(description = "책 소개", example = "토지의 지리적 배경으로....")
    private String description;

    private ImageFileResponse image;

    public static BookListResponse from(Book book) {
        return BookListResponse.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .image(ImageFileResponse.from(book.getImage()))
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .coverUrl(book.getCoverUrl())
                .description(book.getDescription())
                .build();
    }
}
