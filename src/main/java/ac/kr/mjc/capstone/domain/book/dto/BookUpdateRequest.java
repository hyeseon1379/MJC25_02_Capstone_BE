package ac.kr.mjc.capstone.domain.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "도서 정보 수정 요청")
public class BookUpdateRequest {
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

    @Schema(description = "도서 표지 ID", example = "1")
    private Long imageId;

    private List<BookDetailsUpdateRequest> bookDetailsUpdate;
}
