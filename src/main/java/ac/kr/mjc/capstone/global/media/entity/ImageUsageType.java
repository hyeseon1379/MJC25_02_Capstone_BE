package ac.kr.mjc.capstone.global.media.entity;

import lombok.Getter;

@Getter
public enum ImageUsageType {
    BOOK("book"),
    NOTICE("notice"),
    BOARD("board");

    private final String directory;

    ImageUsageType(String directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return directory;
    }

    public static ImageUsageType fromString(String text){
        String upperText = text.toUpperCase(); // 자동으로 소문자를 대문자로 변환
        for (ImageUsageType type : ImageUsageType.values()) {
            if (type.name().equals(upperText)) {
                return type;
            }
        }
        // 일치하는 값이 없을 때
        throw new IllegalArgumentException("Invalid usage type : " + text);
    }
}
