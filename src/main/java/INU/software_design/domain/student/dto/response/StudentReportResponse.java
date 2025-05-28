package INU.software_design.domain.student.dto.response;

import INU.software_design.domain.attendance.dto.AttendanceSummaryRes;
import INU.software_design.domain.counsel.dto.response.CounselListResponse;
import INU.software_design.domain.feedback.dto.response.FeedbackListResponse;
import INU.software_design.domain.score.dto.response.StudentAllScoresResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudentReportResponse {

    private StudentInfoResponse studentInfo;

    private AttendanceSummaryRes attendanceSummary;

    private StudentAllScoresResponse studentScores;

    private FeedbackListResponse feedbackList;

    private CounselListResponse counselList;

    @Builder
    private StudentReportResponse(StudentInfoResponse studentInfo, AttendanceSummaryRes attendanceSummary,
                                  StudentAllScoresResponse studentScores, FeedbackListResponse feedbackList,
                                  CounselListResponse counselList) {
        this.studentInfo = studentInfo;
        this.attendanceSummary = attendanceSummary;
        this.studentScores = studentScores;
        this.feedbackList = feedbackList;
        this.counselList = counselList;
    }

    public static StudentReportResponse create(StudentInfoResponse studentInfo, AttendanceSummaryRes attendanceSummary,
                                               StudentAllScoresResponse studentScores, FeedbackListResponse feedbackList,
                                               CounselListResponse counselList) {
        return StudentReportResponse.builder()
                .studentInfo(studentInfo)
                .attendanceSummary(attendanceSummary)
                .studentScores(studentScores)
                .feedbackList(feedbackList)
                .counselList(counselList)
                .build();
    }
}
