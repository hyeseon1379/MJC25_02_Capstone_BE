package ac.kr.mjc.capstone.global.media.entity;

import lombok.Getter;

@Getter
public enum ImageUsageType {
    BOOK("book"),
    CONTEST("contest"),
    CONTEST_RESULT("contest_result");

    private final String directory;

    ImageUsageType(String directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return directory;
    }
}
