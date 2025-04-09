package INU.software_design.domain.score.dto.response;

import INU.software_design.domain.score.entity.SubjectScore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StudentScoreResponse {

    private Integer totalScore;

    private Integer wholeRank;

    private Integer classRank;

    private List<SubjectScore> subjects;

    private StudentScoreResponse(Integer totalScore, Integer wholeRank, Integer classRank, List<SubjectScore> subjects) {
        this.totalScore = totalScore;
        this.wholeRank = wholeRank;
        this.classRank = classRank;
        this.subjects = subjects;
    }

    public static StudentScoreResponse create(Integer totalScore, Integer wholeRank, Integer classRank, List<SubjectScore> subjects) {
        return new StudentScoreResponse(totalScore, wholeRank, classRank, subjects);
    }
}