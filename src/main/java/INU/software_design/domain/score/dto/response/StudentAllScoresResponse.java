package INU.software_design.domain.score.dto.response;

import INU.software_design.domain.score.entity.SubjectScore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class StudentAllScoresResponse {

    private Map<Integer, Map<Integer, SemesterScore>> scoresByGradeAndSemester;

    private StudentAllScoresResponse(Map<Integer, Map<Integer, SemesterScore>> scoresByGradeAndSemester) {
        this.scoresByGradeAndSemester = scoresByGradeAndSemester;
    }

    public static StudentAllScoresResponse create(Map<Integer, Map<Integer, SemesterScore>> scoresByGradeAndSemester) {
        return new StudentAllScoresResponse(scoresByGradeAndSemester);
    }
}