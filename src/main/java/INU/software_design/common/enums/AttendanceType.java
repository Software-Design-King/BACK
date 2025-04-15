package INU.software_design.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AttendanceType {
    LATE("지각"),
    LEAVE("조퇴"),
    ABSENT("결석"),
	SICK("병결"),
	;

    private final String value;

    AttendanceType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AttendanceType fromValue(String value) {
        for (AttendanceType type : AttendanceType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("정의되지 않은 값 입니다 : " + value);
    }
}
