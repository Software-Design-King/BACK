package INU.software_design.domain.feedback.entity;
import INU.software_design.domain.feedback.dto.request.RegisterFeedRequest;
import INU.software_design.domain.student.entity.Student;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Feedback {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    private Long teacherId;

    private int grade;

    private String title;

    private String score;

    private String behavior;

    private String attendance;

    private String attitude;

    private String others;

    private Boolean isSharedWithStudent;

    private Boolean isSharedWithParent;

    @CreatedDate
    private LocalDateTime createdAt;

    private Feedback(Long studentId, Long teacherId, int grade, String title,
                    String score, String behavior, String attendance,
                     String attitude, String others,
                     Boolean isSharedWithStudent, Boolean isSharedWithParent) {
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.grade = grade;
        this.title = title;
        this.score = score;
        this.behavior = behavior;
        this.attendance = attendance;
        this.attitude = attitude;
        this.others = others;
        this.isSharedWithStudent = isSharedWithStudent;
        this.isSharedWithParent = isSharedWithParent;
    }

    public static Feedback create(Long studentId, Long teacherId, int grade, String title, String score, String behavior, String attendance, String attitude, String others, Boolean isSharedWithStudent, Boolean isSharedWithParent) {
        return new Feedback(studentId, teacherId, grade, title, score, behavior, attendance, attitude, others, isSharedWithStudent, isSharedWithParent);
    }

    public static Feedback create(Student student, Long teacherId, RegisterFeedRequest request){
        return new Feedback(
                student.getId(),
                teacherId,
                student.getGrade(),
                request.getTitle(),
                request.getScoreFeed(),
                request.getBehaviorFeed(),
                request.getAttendanceFeed(),
                request.getAttitudeFeed(),
                request.getOthersFeed(),
                request.isSharedWithStudent(),
                request.isSharedWithParent()
        );
    }
}
