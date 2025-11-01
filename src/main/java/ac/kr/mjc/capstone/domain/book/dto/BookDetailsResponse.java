package ac.kr.mjc.capstone.domain.book.dto;

import ac.kr.mjc.capstone.domain.book.entity.BookDetails;
import ac.kr.mjc.capstone.domain.book.entity.Reader;
import ac.kr.mjc.capstone.domain.children.entity.ChildGender;
import ac.kr.mjc.capstone.domain.children.entity.ChildrenEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookDetailsResponse {
    private Long bookDetailsId;
    private Long readerId;
    private String readerName;
    private String readerImage;
    private String readingStatus;
    private LocalDate startDate;
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