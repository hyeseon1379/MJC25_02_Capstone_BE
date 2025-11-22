package ac.kr.mjc.capstone.domain.calendar.dto;

import ac.kr.mjc.capstone.domain.book.entity.Reader;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "독자 정보 응답")
public class ReaderInfo {
    @Schema(description = "독자 ID", example = "1")
    private Long readerId;

    @Schema(description = "독자 색상값", example = "#FFFFFF")
    private String color;

    @Schema(description = "독자 이름", example = "홍길동")
    private String readerName;

    public static ReaderInfo from(Reader reader) {
        return ReaderInfo.builder()
                .readerId(reader.getReaderId())
                .color(reader.getColor())
                .readerName(reader.getDisplayName())
                .build();
    }
}
