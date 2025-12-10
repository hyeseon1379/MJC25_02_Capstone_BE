package ac.kr.mjc.capstone.domain.share.dto;

import ac.kr.mjc.capstone.domain.share.entity.MeetStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareStatusUpdateRequest {

    @NotNull(message = "상태값은 필수입니다")
    private MeetStatus meetStatus;
}
