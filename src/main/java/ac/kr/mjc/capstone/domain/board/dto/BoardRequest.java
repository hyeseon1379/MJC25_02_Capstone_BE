package ac.kr.mjc.capstone.domain.board.dto;

import ac.kr.mjc.capstone.domain.boardimage.entity.BoardImageEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequest {

    @NotBlank(message = "제목을 입력해주세요")
    @Size(max = 20, message = "제목은 최대 20자까지 가능합니다")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    @Size(max = 1000, message = "내용은 최대 1000자까지 가능합니다")
    private String content;

    private BoardImageEntity boardImage;
}
