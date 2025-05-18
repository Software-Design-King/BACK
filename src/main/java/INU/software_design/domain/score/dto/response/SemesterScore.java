package INU.software_design.domain.score.dto.response;

import INU.software_design.domain.score.entity.SubjectScore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SemesterScore {
    private Integer totalScore;

    private Double averageScore;

    private Integer wholeRank;

    private Integer classRank;

    private List<SubjectScore> subjects;

    private SemesterScore(Integer totalScore, Double averageScore, Integer wholeRank, Integer classRank, List<SubjectScore> subjects) {
        this.totalScore = totalScore;
        this.averageScore = averageScore;
        this.wholeRank = wholeRank;
        this.classRank = classRank;
        this.subjects = subjects;
    }

    public static SemesterScore create(Integer totalScore, Double averageScore, Integer wholeRank, Integer classRank, List<SubjectScore> subjects) {
        return new SemesterScore(totalScore, averageScore, wholeRank, classRank, subjects);
    }
}
