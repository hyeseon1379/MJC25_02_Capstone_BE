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
public class DialogueResponse {
    private Long conversationId;
    private Long userId;
    private DialogueBookInfo book;
    private String title;
    private String content;
    private List<EmotionInfo> emotions;
    private String aiQuestion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DialogueResponse from(DialogueConversation conversation) {
        return DialogueResponse.builder()
                .conversationId(conversation.getConversationId())
                .userId(conversation.getUser().getUserId())
                .book(DialogueBookInfo.from(conversation.getBook()))
                .title(conversation.getTitle())
                .content(conversation.getContent())
                .emotions(conversation.getEmotions().stream()
                        .map(EmotionInfo::from)
                        .collect(Collectors.toList()))
                .aiQuestion(conversation.getAiQuestion())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }
}
