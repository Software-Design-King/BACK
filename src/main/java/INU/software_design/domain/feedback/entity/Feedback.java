package INU.software_design.domain.feedback.entity;
import INU.software_design.domain.feedback.dto.RegisterFeedRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Feedback {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    private Long teacherId;

    private Integer score;

    private String behavior;

    private String attendance;

    private String attitude;

    private String others;

    private Boolean isPublic;

    @CreatedDate
    private LocalDateTime createdAt;

    private Feedback(Long studentId, Long teacherId,
                    String behavior, String attendance,
                     String attitude, String others,
                     Boolean isPublic) {
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.behavior = behavior;
        this.attendance = attendance;
        this.attitude = attitude;
        this.others = others;
        this.isPublic = isPublic;
    }

    public static Feedback create(Long studentId, Long teacherId, String behavior, String attendance, String attitude, String title, Boolean isPublic) {
        return new Feedback(studentId, teacherId, behavior, attendance, attitude, title, isPublic);
    }

    public static Feedback create(Long studentId, Long teacherId, RegisterFeedRequest request){
        return new Feedback(
                studentId, teacherId,
                request.getBehaviorFeed(),
                request.getAttendanceFeed(),
                request.getAttitudeFeed(),
                request.getOthersFeed(),
                request.isPublic()
        );
    }
}
