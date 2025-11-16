package ac.kr.mjc.capstone.domain.notice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeUpdateRequest {

    @Size(max = 100, message = "제목은 100자 이하로 입력해주세요")
    private String title;

    @Size(max = 2000, message = "내용은 2000자 이하로 입력해주세요")
    private String content;

    private Long imageId; // 이미지 ID (선택)
}
