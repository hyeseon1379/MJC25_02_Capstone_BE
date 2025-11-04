package ac.kr.mjc.capstone.domain.book.dto;

import ac.kr.mjc.capstone.domain.book.entity.BookDetails;
import ac.kr.mjc.capstone.domain.book.entity.Reader;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@Schema(description = "도서 상세 정보 응답")
public class BookDetailsResponse {
    @Schema(description = "도서 상세 ID", example = "1")
    private Long bookDetailsId;

    @Schema(description = "독자 ID", example = "1")
    private Long readerId;

    @Schema(description = "독자 이름(닉네임)", example = "홍길동")
    private String readerName;

    @Schema(description = "독자 프로필", example = "https://example.com/images/profile.jpg")
    private String readerImage;

    @Schema(description = "독서 상태", example = "읽는 중")
    private String readingStatus;

    @Schema(description = "독서 시작일", example = "2025-11-03")
    private LocalDate startDate;

    @Schema(description = "독서 종료일", example = "2025-11-10")
    private LocalDate endDate;


    public static BookDetailsResponse from(BookDetails bookDetails) {

        Reader reader = bookDetails.getReader();

        return BookDetailsResponse.builder()
                .bookDetailsId(bookDetails.getDetailsId())
                .readerId(bookDetails.getReader().getReaderId())
                .readerName(reader.getDisplayName())
                .readerImage(reader.getProfileImageUrl())
                .readingStatus(bookDetails.getReadingStatus().getDisplayName())
                .startDate(bookDetails.getStartDate())
                .endDate(bookDetails.getEndDate())
                .build();
    }
}