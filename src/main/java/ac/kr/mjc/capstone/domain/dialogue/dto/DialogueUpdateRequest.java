package ac.kr.mjc.capstone.domain.dialogue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DialogueUpdateRequest {

    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    private String title;

    @NotBlank(message = "대화 내용은 필수입니다")
    private String content;

    @NotEmpty(message = "감정 태그는 최소 1개 이상 선택해야 합니다")
    private List<String> emotions;  // 전체 교체

    @Size(max = 500, message = "AI 질문은 500자를 초과할 수 없습니다")
    private String aiQuestion;
}
