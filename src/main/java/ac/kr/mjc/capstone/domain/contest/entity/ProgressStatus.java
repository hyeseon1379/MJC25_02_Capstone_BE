package ac.kr.mjc.capstone.domain.contest.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ProgressStatus {
    @JsonProperty("planned")
    PLANNED("시작 전"),

    @JsonProperty("ongoing")
    ONGOING("진행 중"),

    @JsonProperty("completed")
    COMPLETED("진행 완료"),

    @JsonProperty("cancelled")
    CANCELLED("진행 마감");

    private final String displayName;

    ProgressStatus(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    @Override
    public String toString() {
        return displayName;
    }
}
