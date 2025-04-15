package INU.software_design.domain.score.entity;
import INU.software_design.common.enums.Subject;
import INU.software_design.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    @Enumerated(value = EnumType.STRING)
    private Subject subject;

    private Integer score;

    private Integer grade;

    private Integer semester;

    private Score(Student student, Subject subject, Integer score, Integer semester) {
        this.studentId = student.getId();
        this.subject = subject;
        this.score = score;
        this.grade = student.getGrade();
        this.semester = semester;
    }

    public static Score create(Student student, Subject subject, SubjectScore request, Integer semester) {
        return new Score(student, subject, request.getScore(), semester);
    }

    public void updateScore(SubjectScore subjectScore) {
        this.score = subjectScore.getScore();
    }
}
