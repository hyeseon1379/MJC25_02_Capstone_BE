package ac.kr.mjc.capstone.domain.packaze.dto;

import ac.kr.mjc.capstone.domain.packaze.entity.Packaze;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "구독 패키지 응답 DTO")
public class PackazeResponse {

    @Schema(description = "패키지 ID", example = "1")
    private Long id;

    @Schema(description = "패키지명", example = "베이직 플랜")
    private String name;

    @Schema(description = "패키지 설명", example = "기본 구독 패키지입니다.")
    private String description;

    @Schema(description = "가격", example = "9900")
    private BigDecimal price;

    @Schema(description = "이용 기간(일)", example = "30")
    private Integer durationDays;

    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive;

    @Schema(description = "생성일시", example = "2024-01-01T00:00:00")
    private LocalDateTime createAt;

    @Schema(description = "수정일시", example = "2024-01-15T10:30:00")
    private LocalDateTime updateAt;

    public static PackazeResponse from(Packaze packaze) {
        return PackazeResponse.builder()
                .id(packaze.getId())
                .name(packaze.getName())
                .description(packaze.getDescription())
                .price(packaze.getPrice())
                .durationDays(packaze.getDurationDays())
                .isActive(packaze.getIsActive())
                .createAt(packaze.getCreateAt())
                .updateAt(packaze.getUpdateAt())
                .build();
    }
}
