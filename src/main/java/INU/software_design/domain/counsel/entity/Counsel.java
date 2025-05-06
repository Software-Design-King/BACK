package INU.software_design.domain.counsel.entity;
import INU.software_design.domain.counsel.dto.request.RegisterCounselRequest;
import INU.software_design.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Counsel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    private Long teacherId;

    private int grade;

    private String content;

    private String plan;

    @ElementCollection
    @CollectionTable(name = "tags", joinColumns = @JoinColumn(name = "id"))
    private List<String> tags;

    private boolean isShared;

    @CreatedDate
    private LocalDateTime createdAt;

    private Counsel(Long studentId, Long teacherId, int grade, String content, String plan, List<String> tags, boolean isShared) {
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.grade = grade;
        this.content = content;
        this.plan = plan;
        this.tags = tags;
        this.isShared = isShared;
    }

    public static Counsel create(Long studentId, Long teacherId, int grade, String content, String plan, List<String> tags, boolean isShared) {
        return new Counsel(studentId, teacherId, grade, content, plan, tags, isShared);
    }

    public static Counsel create(Student student, Long teacherId, RegisterCounselRequest request) {
        return new Counsel(student.getId(), teacherId, student.getGrade(), request.getContext(), request.getPlan(), request.getTags(), request.isShared());
    }
}
