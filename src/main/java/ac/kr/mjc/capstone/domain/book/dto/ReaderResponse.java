package ac.kr.mjc.capstone.domain.book.dto;

import ac.kr.mjc.capstone.domain.book.entity.Reader;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "독자 응답")
public class ReaderResponse {
    @Schema(description = "독자 ID", example = "1")
    private Long readerId;

    @Schema(description = "독자 이름(닉네임)", example = "홍길동")
    private String readerName;

    @Schema(description = "독자 프로필", example = "https://example.com/images/profile.jpg")
    private String readerImage;

    public static ReaderResponse from(Reader reader) {
        return ReaderResponse.builder()
                .readerId(reader.getReaderId())
                .readerName(reader.getDisplayName())
                .readerImage(reader.getProfileImageUrl())
                .build();
    }
}
