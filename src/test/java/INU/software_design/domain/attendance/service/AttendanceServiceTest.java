package INU.software_design.domain.attendance.service;

import INU.software_design.common.enums.AttendanceType;
import INU.software_design.domain.attendance.dto.AttendanceSummaryRes;
import INU.software_design.domain.attendance.entity.Attendance;
import INU.software_design.domain.attendance.repository.AttendanceRepository;
import INU.software_design.domain.student.dto.request.AttendanceRequest;
import INU.software_design.domain.student.dto.response.AttendanceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    @Test
    @DisplayName("학생 출결 조회 테스트")
    void testGetAttendance_Successful() {

        // given
        Long studentId = 1L;
        Attendance attendance1 = Attendance.create(1L, AttendanceType.ABSENT, LocalDateTime.now(), "감기", "감기로 인한 결석");
        Attendance attendance2 = Attendance.create(1L, AttendanceType.LATE, LocalDateTime.now().minusDays(1), "지각", "대중교통 이용으로 인한 지각");

        List<Attendance> attendanceRecords = List.of(attendance1, attendance2);

        Mockito.when(attendanceRepository.findByStudentId(studentId)).thenReturn(attendanceRecords);
        Mockito.when(attendanceRepository.countByStudentIdAndType(studentId, AttendanceType.ABSENT)).thenReturn(1L);
        Mockito.when(attendanceRepository.countByStudentIdAndType(studentId, AttendanceType.LATE)).thenReturn(1L);
        Mockito.when(attendanceRepository.countByStudentIdAndType(studentId, AttendanceType.LEAVE)).thenReturn(0L);
        Mockito.when(attendanceRepository.countByStudentIdAndType(studentId, AttendanceType.SICK)).thenReturn(0L);

        // when
        AttendanceSummaryRes response = attendanceService.getAttendance(studentId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.totalDays()).isEqualTo(2);
        assertThat(response.absentCount()).isEqualTo(1);
        assertThat(response.lateCount()).isEqualTo(1);
        assertThat(response.leaveCount()).isEqualTo(0);
        assertThat(response.sickCount()).isEqualTo(0);
        assertThat(response.details()).hasSize(2);

        verify(attendanceRepository).findByStudentId(studentId);
        verify(attendanceRepository).countByStudentIdAndType(studentId, AttendanceType.ABSENT);
        verify(attendanceRepository).countByStudentIdAndType(studentId, AttendanceType.LATE);
        verify(attendanceRepository).countByStudentIdAndType(studentId, AttendanceType.LEAVE);
        verify(attendanceRepository).countByStudentIdAndType(studentId, AttendanceType.SICK);

        assertThat(response.details()).anyMatch(detail ->
                detail.type() == AttendanceType.ABSENT
                        && detail.reason().equals("감기로 인한 결석")
        );
        assertThat(response.details()).anyMatch(detail ->
                detail.type() == AttendanceType.LATE
                        && detail.reason().equals("대중교통 이용으로 인한 지각")
        );
    }

    @Test
    @DisplayName("학생 성적 조회 실패 - 학생 조회 실패")
    void testGetAttendance_Failure() {
        // given
        Long studentId = 99L;

        Mockito.when(attendanceRepository.findByStudentId(studentId)).thenReturn(List.of());
        Mockito.when(attendanceRepository.countByStudentIdAndType(studentId, AttendanceType.ABSENT)).thenReturn(0L);
        Mockito.when(attendanceRepository.countByStudentIdAndType(studentId, AttendanceType.LATE)).thenReturn(0L);
        Mockito.when(attendanceRepository.countByStudentIdAndType(studentId, AttendanceType.LEAVE)).thenReturn(0L);
        Mockito.when(attendanceRepository.countByStudentIdAndType(studentId, AttendanceType.SICK)).thenReturn(0L);

        // when
        AttendanceSummaryRes response = attendanceService.getAttendance(studentId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.totalDays()).isEqualTo(0);
        assertThat(response.absentCount()).isEqualTo(0);
        assertThat(response.lateCount()).isEqualTo(0);
        assertThat(response.leaveCount()).isEqualTo(0);
        assertThat(response.sickCount()).isEqualTo(0);
        assertThat(response.details()).isEmpty();
    }

    @Test
    @DisplayName("학생 출결 등록 테스트 - 성공")
    void testRegisterAttendance_Successful() {
        // given
        Long studentId = 1L;
        AttendanceRequest request = AttendanceRequest.builder()
                .type(AttendanceType.LATE)
                .date(LocalDate.of(2025, 6, 1).atStartOfDay())
                .title("지각")
                .reason("늦잠")
                .build();

        Attendance attendance = Attendance.of(studentId, request);
        Mockito.when(attendanceRepository.save(Mockito.any(Attendance.class))).thenReturn(attendance);

        // when
        AttendanceResponse response = attendanceService.registerAttendance(studentId, request);

        // then
        assertThat(response).isNotNull().withFailMessage("출결 등록 응답이 null이어서는 안 됩니다.");
        assertThat(response.getType()).isEqualTo(request.getType())
                .withFailMessage("등록된 출결 유형이 요청과 일치해야 합니다.");
        assertThat(response.getDate()).isEqualTo(request.getDate().toString())
                .withFailMessage("등록된 출결 날짜가 요청과 일치해야 합니다.");
        assertThat(response.getTitle()).isEqualTo(request.getTitle())
                .withFailMessage("등록된 출결 제목이 요청과 일치해야 합니다.");
        assertThat(response.getReason()).isEqualTo(request.getReason())
                .withFailMessage("등록된 출결 사유가 요청과 일치해야 합니다.");

        verify(attendanceRepository).save(Mockito.any(Attendance.class));
    }

    @Test
    @DisplayName("학생 출결 등록 테스트 - 실패")
    void testRegisterAttendance_Failure() {
        // given
        Long studentId = 1L;
        AttendanceRequest request = AttendanceRequest.builder()
                .type(AttendanceType.LATE)
                .date(LocalDate.of(2025, 6, 1).atStartOfDay())
                .title("지각")
                .reason("늦잠")
                .build();

        Mockito.when(attendanceRepository.save(Mockito.any(Attendance.class)))
                .thenThrow(new RuntimeException("Database error"));

        // when / then
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            attendanceService.registerAttendance(studentId, request);
        });

        assertThat(exception.getMessage()).isEqualTo("Database error")
                .withFailMessage("예외 메시지가 'Database error'이어야 합니다.");

        verify(attendanceRepository).save(Mockito.any(Attendance.class));
    }
}