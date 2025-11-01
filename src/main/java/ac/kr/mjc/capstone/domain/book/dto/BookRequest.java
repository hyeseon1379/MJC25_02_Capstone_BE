package ac.kr.mjc.capstone.domain.book.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BookRequest {
    @NotBlank(message = "도서 제목을 입력해주세요")
    private String title;

    @NotBlank(message = "도서 저자를 입력해주세요")
    private String author;

    @NotBlank(message = "도서 출판사를 입력해주세요")
    private String publisher;
    @NotBlank(message = "도서 표지 url을 입력해주세요")
    private String imgUrl;

    private List<BookDetailsRequest> bookDetails;
}
