package ac.kr.mjc.capstone.domain.dialogue.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dialogue_emotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DialogueEmotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emotion_id")
    private Long emotionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private DialogueConversation conversation;

    @Column(name = "emotion_type", nullable = false, length = 20)
    private String emotionType;

    // EmotionType enum으로 변환
    public EmotionType getEmotionTypeEnum() {
        return EmotionType.fromCode(this.emotionType);
    }
}
