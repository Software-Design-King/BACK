package INU.software_design.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ExamType {
    MID("중간고사"),
    FINAL("기말고사");

    private final String value;

    ExamType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ExamType gender(String value) {
        for (ExamType examType : ExamType.values()) {
            if (examType.value.equals(value) || examType.name().equals(value)) {
                return examType;
            }
        }
        throw new IllegalArgumentException("정의되지 않은 값 입니다 : " + value);
    }
}
