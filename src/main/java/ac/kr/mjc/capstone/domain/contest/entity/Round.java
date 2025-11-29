package ac.kr.mjc.capstone.domain.contest.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Round {
    @JsonProperty("round_1")
    ROUND_1("1차"),

    @JsonProperty("round_2")
    ROUND_2("2차"),

    @JsonProperty("round_3")
    ROUND_3("3차"),

    @JsonProperty("final")
    FINAL("최종");

    private final String displayName;

    Round(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    @Override
    public String toString() {
        return displayName;
    }
}
