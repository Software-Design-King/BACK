package INU.software_design.domain.score.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubjectScore {

    private String name;

    private Integer score;

    private SubjectScore(String name, Integer score) {
        this.name = name;
        this.score = score;
    }

    public static SubjectScore of(Score score) {
        return new SubjectScore(score.getSubject().toString(), score.getScore());
    }
}
