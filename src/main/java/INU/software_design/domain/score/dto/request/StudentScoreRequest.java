package INU.software_design.domain.score.dto.request;

import INU.software_design.domain.score.entity.SubjectScore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StudentScoreRequest {

    private int semester;

    private List<SubjectScore> subjects;
}
