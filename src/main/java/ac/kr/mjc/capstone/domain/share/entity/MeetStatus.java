package ac.kr.mjc.capstone.domain.share.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetStatus {
    SHARING("SHARING", "나눔중"),
    RESERVED("RESERVED", "예약중"),
    COMPLETED("COMPLETED", "완료");

    private final String value;
    private final String description;

    @JsonValue
    public String getValue() {
        return value;
    }
}
