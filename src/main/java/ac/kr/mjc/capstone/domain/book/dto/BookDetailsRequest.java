package ac.kr.mjc.capstone.domain.book.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BookDetailsRequest {
    private Long childId;
    private LocalDate startDate;
    private LocalDate endDate;
}
