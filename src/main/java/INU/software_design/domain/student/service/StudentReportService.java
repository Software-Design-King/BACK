package INU.software_design.domain.student.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentReportService {

    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final ScoreService scoreService;
    private final FeedbackService feedbackService;
    private final CounselService counselService;

    @Transactional
    public StudentReportResponse reportStudent(Long studentId) {
        return StudentReportResponse.create(
                studentService.getStudentInfo(studentId),
                attendanceService.getAttendance(studentId),
                scoreService.getScore(studentId),
                feedbackService.getFeedbackList(studentId, 0),
                counselService.getCounselList(studentId, 0)
        );
    }
}
