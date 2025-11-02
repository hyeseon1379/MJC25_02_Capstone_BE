package ac.kr.mjc.capstone.domain.board.dto;

import ac.kr.mjc.capstone.domain.boardimage.entity.BoardImageEntity;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardUpdateRequest {

    @Size(max = 20, message = "제목은 최대 20자까지 가능합니다")
    private String title;

    @Size(max = 1000, message = "내용은 최대 1000자까지 가능합니다")
    private String content;

    private BoardImageEntity boardImage;
}
