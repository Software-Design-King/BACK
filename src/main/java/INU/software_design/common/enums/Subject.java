package INU.software_design.common.enums;

import INU.software_design.domain.score.entity.SubjectScore;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Subject {
    KOREAN("국어"),
    ENGLISH("영어"),
    MATH("수학"),
    SCIENCE("과학"),
    SOCIAL("사회"),
    PHYSICAL_EDUCATION("체육"),;

    private final String value;

    Subject(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Subject fromValue(String value) {
        for (Subject subject : Subject.values()) {
            if (subject.value.equals(value)) {
                return subject;
            }
        }
        throw new IllegalArgumentException("정의되지 않은 값 입니다 : " + value);
    }

    public static Subject from(SubjectScore request) {
        String name = request.getName();
        try {
            return Subject.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 과목명 : " + name);
        }
    }
}
