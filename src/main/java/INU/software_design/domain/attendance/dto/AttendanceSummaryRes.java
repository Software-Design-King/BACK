package INU.software_design.domain.attendance.dto;

import INU.software_design.common.enums.AttendanceType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;


public record AttendanceSummaryRes(
        int totalDays,
        int absentCount,
        int lateCount,
        int leaveCount,
        int sickCount,
        List<AttendanceDetail> details
) {
    public record AttendanceDetail(
            AttendanceType type,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            LocalDateTime date,

            String reason
    ) {}
}
