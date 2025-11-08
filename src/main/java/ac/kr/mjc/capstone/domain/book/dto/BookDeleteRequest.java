package ac.kr.mjc.capstone.domain.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "도서 리스트 삭제 요청")
public class BookDeleteRequest {
    @Schema(description = "도서 IDs", example = "1, 2")
    private List<Long> bookIds;
}
