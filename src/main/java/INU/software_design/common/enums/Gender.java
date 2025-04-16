package INU.software_design.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    FEMALE("여자"),
    MALE("남자");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Gender gender(String value) {
        for (Gender gender : Gender.values()) {
            if (gender.value.equals(value) || gender.name().equals(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("정의되지 않은 값 입니다 : " + value);
    }
}
