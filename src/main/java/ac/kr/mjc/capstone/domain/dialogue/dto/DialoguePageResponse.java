package ac.kr.mjc.capstone.domain.dialogue.dto;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DialoguePageResponse {
    private List<DialogueListResponse> conversations;
    private PaginationInfo pagination;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationInfo {
        private int currentPage;
        private int pageSize;
        private int totalPages;
        private long totalElements;
    }

    public static DialoguePageResponse from(Page<DialogueListResponse> page) {
        return DialoguePageResponse.builder()
                .conversations(page.getContent())
                .pagination(PaginationInfo.builder()
                        .currentPage(page.getNumber() + 1)  // 1-based
                        .pageSize(page.getSize())
                        .totalPages(page.getTotalPages())
                        .totalElements(page.getTotalElements())
                        .build())
                .build();
    }
}
