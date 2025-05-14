package INU.software_design.domain.feedback.dto.response;

import INU.software_design.domain.feedback.entity.Feedback;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FeedbackInfoResponse {

    private int grade;

    private LocalDateTime createdAt;

    private String title;

    private String scoreFeed;

    private String behaviorFeed;

    private String attendanceFeed;

    private String attitudeFeed;

    private String othersFeed;

    private boolean isSharedWithStudent;

    private boolean isSharedWithParent;

    private FeedbackInfoResponse(int grade, LocalDateTime createdAt, String title, String scoreFeed, String behaviorFeed, String attendanceFeed, String attitudeFeed, String othersFeed, boolean isSharedWithStudent, boolean isSharedWithParent) {
        this.grade = grade;
        this.createdAt = createdAt;
        this.title = title;
        this.scoreFeed = scoreFeed;
        this.behaviorFeed = behaviorFeed;
        this.attendanceFeed = attendanceFeed;
        this.attitudeFeed = attitudeFeed;
        this.othersFeed = othersFeed;
        this.isSharedWithStudent = isSharedWithStudent;
        this.isSharedWithParent = isSharedWithParent;
    }

    public static FeedbackInfoResponse create(Feedback feedback) {
        return new FeedbackInfoResponse(
                feedback.getGrade(),
                feedback.getCreatedAt(),
                feedback.getTitle(),
                feedback.getScore(),
                feedback.getBehavior(),
                feedback.getAttendance(),
                feedback.getAttitude(),
                feedback.getOthers(),
                feedback.getIsSharedWithStudent(),
                feedback.getIsSharedWithParent()
        );
    }
}
