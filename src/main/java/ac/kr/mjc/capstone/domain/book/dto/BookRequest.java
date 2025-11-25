package ac.kr.mjc.capstone.domain.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "도서 정보 생성 요청")
public class BookRequest {
    @NotBlank(message = "도서 제목을 입력해주세요")
    @Size(max = 255, message = "도서 제목은 최대 255자까지 가능합니다")
    @Schema(description = "도서 제목", example = "토지")
    private String title;

    @Size(max = 100, message = "도서 저자는 최대 100자까지 가능합니다")
    @Schema(description = "도서 저자", example = "박경리")
    private String author;

    @Size(max = 100, message = "도서 출판사는 최대 100자까지 가능합니다")
    @Schema(description = "도서 출판사", example = "마로니에북스")
    private String publisher;

    @Size(max = 13, message = "isbn번호는 13자 입니다.")
    @Schema(description = "isbn번호", example = "9783929979701")
    private String isbn;

    @Size(max = 4, message = "출판년도는 4자 입니다.")
    @Schema(description = "출판년도", example = " 1994")
    private String publicationYear;

    @Size(max = 500, message = "표지 이미지 URL은 최대 500자까지 가능합니다")
    @Schema(description = "표지 이미지 URL", example = "https://....")
    private String coverUrl;

    // DB컬럼 TEXT타입으로 길이 제한 없음
    @Schema(description = "책 소개", example = "토지의 지리적 배경으로....")
    private String description;

    @Schema(description = "도서 표지 ID", example = "1")
    private Long imageId;

    private List<BookDetailsRequest> bookDetails;
}
