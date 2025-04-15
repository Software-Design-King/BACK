package INU.software_design.domain.score;

import INU.software_design.domain.score.dto.ScoreDetailRes;
import INU.software_design.domain.score.entity.Score;
import INU.software_design.domain.student.StudentRepository;
import INU.software_design.domain.student.entity.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;

    public ScoreDetailRes getScoreDetail(final long studentId, final int grade, final int semester) {

        // 1. 점수 가져오기
        List<Score> scores = scoreRepository.findByStudentIdAndGradeAndSemester(studentId, grade, semester);

        int total = scores.stream().mapToInt(Score::getScore).sum();
        double avg = total / (double) scores.size();

        List<ScoreDetailRes.SubjectScore> subjectScores = scores.stream()
                .map(s -> new ScoreDetailRes.SubjectScore(s.getSubject().name(), s.getScore()))
                .toList();

        // 2. 학생 정보 가져오기 (반 정보 필요)
        Student student = studentRepository.findById(studentId).orElseThrow();

        // 3. 전교 석차 계산
        List<Object[]> allTotalScores = scoreRepository.findAllTotalScoresByGradeAndSemester(grade, semester);
        int wholeRank = getRank(studentId, allTotalScores);

        // 4. 반 석차 계산
        List<Object[]> classTotalScores = scoreRepository.findAllTotalScoresByClassAndSemester(grade, semester, student.getClassId());
        int classRank = getRank(studentId, classTotalScores);

        return new ScoreDetailRes(total, avg, wholeRank, classRank, subjectScores);
    }


    private int getRank(Long studentId, List<Object[]> scoreList) {
        List<Long> sortedStudentIds = scoreList.stream()
                .sorted((a, b) -> Long.compare((Long) b[1], (Long) a[1])) // 총점 기준 내림차순 정렬
                .map(e -> (Long) e[0])
                .toList();
        return sortedStudentIds.indexOf(studentId) + 1;
    }

}
