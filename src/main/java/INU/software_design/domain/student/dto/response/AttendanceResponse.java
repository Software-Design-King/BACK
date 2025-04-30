package INU.software_design.domain.student.dto.response;

import INU.software_design.common.enums.AttendanceType;
import INU.software_design.domain.attendance.entity.Attendance;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AttendanceResponse {

    private AttendanceType type;

    private String date;

    private String title;

    private String reason;

    @Builder
    private AttendanceResponse(AttendanceType type, String date, String title, String reason) {
        this.type = type;
        this.date = date;
        this.title = title;
        this.reason = reason;
    }

    public static AttendanceResponse of(Attendance attendance) {
        return AttendanceResponse.builder()
                .type(attendance.getType())
                .date(attendance.getDate().toString())
                .title(attendance.getTitle())
                .reason(attendance.getReason())
                .build();
    }
}
