package ac.kr.mjc.capstone.domain.book.dto;

import ac.kr.mjc.capstone.domain.book.entity.Book;
import ac.kr.mjc.capstone.global.media.dto.ImageFileResponse;
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

    @Schema(description = "isbn번호", example = "9783929979701")
    private String isbn;

    @Schema(description = "출판년도", example = " 1994")
    private String publicationYear;

    @Schema(description = "표지 이미지 URL", example = "https://....")
    private String coverUrl;

    @Schema(description = "책 소개", example = "토지의 지리적 배경으로....")
    private String description;

    private ImageFileResponse image;

    private List<BookDetailsResponse> bookDetails;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .coverUrl(book.getCoverUrl())
                .description(book.getDescription())
                .image(ImageFileResponse.from(book.getImage()))
                .bookDetails(book.getBookDetails().stream()
                        .map(BookDetailsResponse::from).collect(Collectors.toList()))
                .build();
    }
}
