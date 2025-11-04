package ac.kr.mjc.capstone.domain.book.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ReaderType {
    @JsonProperty("adult")
    ADULT("부모"),

    @JsonProperty("child")
    CHILD("자식");

    private final String displayName;

    ReaderType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    @Override
    public String toString() {
        return displayName;
    }
}
