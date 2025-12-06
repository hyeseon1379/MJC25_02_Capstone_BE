package ac.kr.mjc.capstone.domain.dialogue.dto;

import ac.kr.mjc.capstone.domain.dialogue.entity.DialogueConversation;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DialogueListResponse {
    private Long conversationId;
    private DialogueBookInfo book;
    private String title;
    private String contentSummary;  // 앞 50자
    private List<EmotionInfo> emotions;
    private LocalDateTime createdAt;

    public static DialogueListResponse from(DialogueConversation conversation) {
        String summary = conversation.getContent();
        if (summary != null && summary.length() > 50) {
            summary = summary.substring(0, 50) + "...";
        }

        return DialogueListResponse.builder()
                .conversationId(conversation.getConversationId())
                .book(DialogueBookInfo.from(conversation.getBook()))
                .title(conversation.getTitle())
                .contentSummary(summary)
                .emotions(conversation.getEmotions().stream()
                        .map(EmotionInfo::from)
                        .collect(Collectors.toList()))
                .createdAt(conversation.getCreatedAt())
                .build();
    }
}
