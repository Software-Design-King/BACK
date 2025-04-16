package INU.software_design.domain.score.entity;

import INU.software_design.common.enums.Subject;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubjectScore {

    private Subject name;

    private Integer score;

    private SubjectScore(Subject name, Integer score) {
        this.name = name;
        this.score = score;
    }

    public static SubjectScore create(Score score) {
        return new SubjectScore(score.getSubject(), score.getScore());
    }
}
