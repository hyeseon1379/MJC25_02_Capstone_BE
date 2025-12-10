package ac.kr.mjc.capstone.domain.dialogue.dto;

import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DialogueSearchResponse {
    private List<DialogueListResponse> conversations;
    private DialoguePageResponse.PaginationInfo pagination;
    private SearchCriteria searchCriteria;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchCriteria {
        private String keyword;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<String> emotions;
    }

    public static DialogueSearchResponse from(Page<DialogueListResponse> page, 
                                               String keyword, 
                                               LocalDate startDate, 
                                               LocalDate endDate, 
                                               List<String> emotions) {
        return DialogueSearchResponse.builder()
                .conversations(page.getContent())
                .pagination(DialoguePageResponse.PaginationInfo.builder()
                        .currentPage(page.getNumber() + 1)
                        .pageSize(page.getSize())
                        .totalPages(page.getTotalPages())
                        .totalElements(page.getTotalElements())
                        .build())
                .searchCriteria(SearchCriteria.builder()
                        .keyword(keyword)
                        .startDate(startDate)
                        .endDate(endDate)
                        .emotions(emotions)
                        .build())
                .build();
    }
}
