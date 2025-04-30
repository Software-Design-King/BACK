package INU.software_design.domain.score.dto.request;

import INU.software_design.domain.score.entity.SubjectScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentScoreRequest {

    private int semester;

    private List<SubjectScore> subjects;
}
