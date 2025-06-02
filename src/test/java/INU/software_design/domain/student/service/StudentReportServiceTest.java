package INU.software_design.domain.student.service;

import INU.software_design.common.enums.Gender;
import INU.software_design.common.exception.SwPlanException;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import INU.software_design.domain.attendance.dto.AttendanceSummaryRes;
import INU.software_design.domain.attendance.service.AttendanceService;
import INU.software_design.domain.counsel.dto.response.CounselListResponse;
import INU.software_design.domain.counsel.service.CounselService;
import INU.software_design.domain.feedback.dto.response.FeedbackListResponse;
import INU.software_design.domain.feedback.service.FeedbackService;
import INU.software_design.domain.score.dto.response.StudentAllScoresResponse;
import INU.software_design.domain.score.service.ScoreService;
import INU.software_design.domain.student.dto.response.StudentInfoResponse;
import INU.software_design.domain.student.dto.response.StudentReportResponse;
import INU.software_design.domain.student.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentReportServiceTest {

    @InjectMocks
    private StudentReportService studentReportService;

    @Mock
    private StudentService studentService;

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private ScoreService scoreService;

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private CounselService counselService;

    private Student student;

    @BeforeEach
    void setUp() {
        int grade = 3;
        Long classId = 100L;
        student = Student.create(
                classId, "홍길동", 15, grade,
                "서울시", 10, "20231234", Gender.MALE,
                LocalDate.of(2009, 5, 12), "010-1111-2222", "010-3333-4444", "11112222"
        );
    }

    @Test
    @DisplayName("보고서 생성 테스트 - 성공")
    void testReportStudentSuccess() {
        // given
        Long studentId = 1L;

        StudentInfoResponse studentInfoResponse = StudentInfoResponse.of(student, 100);
        AttendanceSummaryRes attendanceSummaryRes = new AttendanceSummaryRes(
                30, 2, 1, 0, 1, List.of()
        );
        StudentAllScoresResponse studentAllScoresResponse = new StudentAllScoresResponse();
        FeedbackListResponse feedbackListResponse = new FeedbackListResponse();
        CounselListResponse counselListResponse = new CounselListResponse();

        when(studentService.getStudentInfo(studentId)).thenReturn(studentInfoResponse);
        when(attendanceService.getAttendance(studentId)).thenReturn(attendanceSummaryRes);
        when(scoreService.getScore(studentId)).thenReturn(studentAllScoresResponse);
        when(feedbackService.getFeedbackList(studentId, 0)).thenReturn(feedbackListResponse);
        when(counselService.getCounselList(studentId, 0)).thenReturn(counselListResponse);

        // when
        StudentReportResponse result = studentReportService.reportStudent(studentId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStudentInfo()).isEqualTo(studentInfoResponse);
        assertThat(result.getAttendanceSummary()).isEqualTo(attendanceSummaryRes);
        assertThat(result.getStudentScores()).isEqualTo(studentAllScoresResponse);
        assertThat(result.getFeedbackList()).isEqualTo(feedbackListResponse);
        assertThat(result.getCounselList()).isEqualTo(counselListResponse);
    }

    @Test
    @DisplayName("보고서 생성 테스트 - 실패 학생 조회 실패")
    void testReportStudentFailure() {
        // given
        Long studentId = 1L;
        when(studentService.getStudentInfo(studentId)).thenThrow(new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));

        // when
        Exception exception = assertThrows(SwPlanUseException.class, () -> studentReportService.reportStudent(studentId));

        // then
        assertThat(exception.getMessage()).isNull();
    }
}
