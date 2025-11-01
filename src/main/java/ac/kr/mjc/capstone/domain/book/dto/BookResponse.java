package ac.kr.mjc.capstone.domain.book.dto;

import ac.kr.mjc.capstone.domain.book.entity.Book;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class BookResponse {
    private Long BookId;
    private String title;
    private String author;
    private String publisher;
    private String imgUrl;

    private List<BookDetailsResponse> bookDetails;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .BookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .imgUrl(book.getImgUrl())
                .bookDetails(book.getBookDetails().stream()
                        .map(BookDetailsResponse::from).collect(Collectors.toList()))
                .build();
    }
}
