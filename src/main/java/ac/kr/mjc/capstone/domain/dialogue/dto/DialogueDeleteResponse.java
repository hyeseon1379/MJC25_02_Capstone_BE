package ac.kr.mjc.capstone.domain.dialogue.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DialogueDeleteResponse {
    private Long deletedConversationId;

    public static DialogueDeleteResponse of(Long conversationId) {
        return DialogueDeleteResponse.builder()
                .deletedConversationId(conversationId)
                .build();
    }
}
