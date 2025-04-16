package INU.software_design.domain.attendance.service;

import INU.software_design.common.enums.AttendanceType;
import INU.software_design.domain.attendance.dto.AttendanceSummaryRes;
import INU.software_design.domain.attendance.entity.Attendance;
import INU.software_design.domain.attendance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    public AttendanceSummaryRes getAttendance(final Long studentId) {
        List<Attendance> records = attendanceRepository.findByStudentId(studentId);

        long absent = attendanceRepository.countByStudentIdAndType(studentId, AttendanceType.ABSENT);
        long late = attendanceRepository.countByStudentIdAndType(studentId, AttendanceType.LATE);
        long leave = attendanceRepository.countByStudentIdAndType(studentId, AttendanceType.LEAVE);
        long sick = attendanceRepository.countByStudentIdAndType(studentId, AttendanceType.SICK);

        List<AttendanceSummaryRes.AttendanceDetail> details = records.stream()
                .map(att -> new AttendanceSummaryRes.AttendanceDetail(
                        att.getType(),
                        att.getDate(),
                        att.getReason()
                )).toList();

        return new AttendanceSummaryRes(
                records.size(),
                (int) absent,
                (int) late,
                (int) leave,
                (int) sick,
                details
        );
    }
}
