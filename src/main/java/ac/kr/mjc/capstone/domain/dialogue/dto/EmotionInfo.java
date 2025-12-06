package ac.kr.mjc.capstone.domain.dialogue.dto;

import ac.kr.mjc.capstone.domain.dialogue.entity.DialogueEmotion;
import ac.kr.mjc.capstone.domain.dialogue.entity.EmotionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionInfo {
    private Long emotionId;
    private String type;
    private String label;
    private String emoji;

    public static EmotionInfo from(DialogueEmotion emotion) {
        EmotionType emotionType = emotion.getEmotionTypeEnum();
        return EmotionInfo.builder()
                .emotionId(emotion.getEmotionId())
                .type(emotionType.getCode())
                .label(emotionType.getLabel())
                .emoji(emotionType.getEmoji())
                .build();
    }
}
