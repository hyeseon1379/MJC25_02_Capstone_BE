package ac.kr.mjc.capstone.domain.dialogue.entity;

import ac.kr.mjc.capstone.domain.book.entity.Book;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dialogue_conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DialogueConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long conversationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "ai_question", length = 500)
    private String aiQuestion;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DialogueEmotion> emotions = new ArrayList<>();

    // 감정 태그 추가 헬퍼 메서드
    public void addEmotion(EmotionType emotionType) {
        DialogueEmotion emotion = DialogueEmotion.builder()
                .conversation(this)
                .emotionType(emotionType.getCode())
                .build();
        this.emotions.add(emotion);
    }

    // 감정 태그 전체 교체
    public void replaceEmotions(List<String> emotionCodes) {
        this.emotions.clear();
        for (String code : emotionCodes) {
            addEmotion(EmotionType.fromCode(code));
        }
    }

    // 제목 자동 생성 (content 앞 20자 + ...)
    public void generateTitleIfEmpty() {
        if (this.title == null || this.title.isBlank()) {
            if (this.content != null && this.content.length() > 20) {
                this.title = this.content.substring(0, 20) + "...";
            } else {
                this.title = this.content;
            }
        }
    }
}
