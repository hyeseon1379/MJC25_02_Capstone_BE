package ac.kr.mjc.capstone.domain.dialogue.entity;

import lombok.Getter;

@Getter
public enum EmotionType {
    HAPPY("happy", "즐거움", "🙂"),
    NORMAL("normal", "보통", "😐"),
    TOUCHED("touched", "감동", "🥹"),
    DIFFICULT("difficult", "어려움", "😵"),
    CURIOUS("curious", "궁금함", "🤔"),
    GROWTH("growth", "성장", "🌱");

    private final String code;
    private final String label;
    private final String emoji;

    EmotionType(String code, String label, String emoji) {
        this.code = code;
        this.label = label;
        this.emoji = emoji;
    }

    public static EmotionType fromCode(String code) {
        for (EmotionType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown emotion type: " + code);
    }
}
