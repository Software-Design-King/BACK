package INU.software_design.domain.score.entity;

import INU.software_design.common.enums.ExamType;
import INU.software_design.common.enums.Subject;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubjectScore {

    private Subject name;

    private Integer score;

    private ExamType examType;

    private SubjectScore(Subject name, Integer score, ExamType examType) {
        this.name = name;
        this.score = score;
        this.examType = examType;
    }

    public static SubjectScore create(Score score) {
        return new SubjectScore(score.getSubject(), score.getScore(), score.getExamType());
    }
}
