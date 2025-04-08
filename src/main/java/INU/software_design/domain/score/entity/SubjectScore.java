package INU.software_design.domain.score.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubjectScore {

    private String name;

    private Integer score;

    public SubjectScore(String name, Integer score) {
        this.name = name;
        this.score = score;
    }
}
