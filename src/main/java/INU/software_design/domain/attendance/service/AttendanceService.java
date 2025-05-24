package INU.software_design.domain.attendance.service;

import INU.software_design.common.enums.AttendanceType;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import INU.software_design.domain.attendance.dto.AttendanceSummaryRes;
import INU.software_design.domain.attendance.entity.Attendance;
import INU.software_design.domain.attendance.repository.AttendanceRepository;
import INU.software_design.domain.student.dto.request.AttendanceRequest;
import INU.software_design.domain.student.dto.response.AttendanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Transactional
    public AttendanceResponse registerAttendance(Long studentId, AttendanceRequest request) {
        Attendance attendance = Attendance.of(studentId, request);
        attendanceRepository.save(attendance);
        return AttendanceResponse.of(attendance);
    }

    @Transactional
    public AttendanceResponse updateAttendance(Long studentId, AttendanceRequest request) {
        Attendance attendance = findAttendanceBy(studentId, request);
        attendance.update(request);
        return AttendanceResponse.of(attendance);
    }

    @Transactional
    public void deleteAttendance(Long studentId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        List<Attendance> attendances = attendanceRepository.findByStudentIdAndDateBetween(studentId, start, end);

        if (attendances.isEmpty()) {
            throw new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY);
        }
        attendanceRepository.deleteAll(attendances);
    }

    private Attendance findAttendanceBy(Long studentId, AttendanceRequest request) {
        return attendanceRepository.findByStudentIdAndDate(studentId, request.getDate())
                .orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));
    }

}
