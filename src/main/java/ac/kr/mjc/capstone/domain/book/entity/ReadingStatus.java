package ac.kr.mjc.capstone.domain.book.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ReadingStatus {
    @JsonProperty("to_read")
    TO_READ("읽기 전"),

    @JsonProperty("reading")
    READING("읽는 중"),

    @JsonProperty("completed")
    COMPLETED("읽음");

    private final String displayName;

    ReadingStatus(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    @Override
    public String toString() {
        return displayName;
    }
}
