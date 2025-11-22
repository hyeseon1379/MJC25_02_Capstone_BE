package ac.kr.mjc.capstone.domain.book.dto;

import ac.kr.mjc.capstone.domain.book.entity.ReadingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "도서 카테고리 조회 요청")
public class BookSearchRequest {
    @Schema(description = "독서 상태", example = "READING")
    private ReadingStatus readingStatus;

    @Schema(description = "독자 ID", example = "1")
    private Long readerId;
}