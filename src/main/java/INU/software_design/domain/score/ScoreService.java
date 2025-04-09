package INU.software_design.domain.score;

import INU.software_design.common.enums.Subject;
import INU.software_design.domain.score.entity.Score;
import INU.software_design.domain.score.entity.SubjectScore;
import INU.software_design.domain.student.StudentRepository;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.score.dto.request.StudentScoreRequest;
import INU.software_design.domain.score.dto.response.StudentScoreResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScoreService {

    private final StudentRepository studentRepository;
    private final ScoreRepository scoreRepository;

    @Transactional
    public StudentScoreResponse registerStudentScore(Long studentId, StudentScoreRequest request) {
        Student student = findStudentBy(studentId);
        Integer semester = request.getSemester();

        saveStudentScores(request, student, semester);

        return StudentScoreResponse.create(
                getTotalScore(studentId, semester),
                getWholeRankBy(studentId, semester),
                getClassRankBy(studentId, semester, student),
                getSubjectScores(studentId, semester)
        );
    }

    // Q. 업데이트 부분 수정
    @Transactional
    public StudentScoreResponse updateStudentScore(Long studentId, StudentScoreRequest request) {
        Student student = findStudentBy(studentId);
        Integer semester = request.getSemester();

        updateStudentScores(request, student, semester);

        return StudentScoreResponse.create(
                getTotalScore(studentId, semester),
                getWholeRankBy(studentId, semester),
                getClassRankBy(studentId, semester, student),
                getSubjectScores(studentId, semester)
        );
    }

    // Q. 전체 삭제?? 부분 삭제??
    @Transactional
    public void deleteStudentScore(Long studentId, Integer semester) {
        Student student = findStudentBy(studentId);
        List<Score> scores = getScoreList(student.getId(), semester);
        scoreRepository.deleteAll(scores);
    }

    private void updateStudentScores(StudentScoreRequest request, Student student, Integer semester) {
        List<Score> StudentScores = getScoreList(student.getId(), semester);

        for (SubjectScore subjectScore : request.getSubjects()) {
            Subject subject = Subject.from(subjectScore);
            Score Score = StudentScores.stream()
                    .filter(score -> score.getSubject().equals(subject))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("해당 과목의 성적을 찾을 수 없습니다."));
            Score.updateScore(subjectScore);
        }
    }

    private List<SubjectScore> getSubjectScores(Long studentId, Integer semester) {
        List<Score> scoreList = getScoreList(studentId, semester);
        return scoreList.stream()
                .map(SubjectScore::create)
                .toList();
    }

    private List<Score> getScoreList(Long studentId, Integer semester) {
        if (scoreRepository.findAllByStudentIdAndSemester(studentId, semester).isEmpty()) {
            throw new EntityNotFoundException("해당 학생의 성적을 찾을 수 없습니다.");
        } else {
            return scoreRepository.findAllByStudentIdAndSemester(studentId, semester);
        }
    }

    private Integer getClassRankBy(Long studentId, Integer semester, Student student) {
        return scoreRepository.findClassRankBy(semester, student.getGrade(), studentId);
    }

    private Integer getWholeRankBy(Long studentId, Integer semester) {
        return scoreRepository.findWholeRankBy(semester, studentId);
    }

    private Integer getTotalScore(Long studentId, Integer semester) {
        return scoreRepository.findTotalScoreBy(semester, studentId);
    }

    private void saveStudentScores(StudentScoreRequest request, Student student, Integer semester) {
        for (SubjectScore subjectScore : request.getSubjects()) {
            Subject subject = Subject.from(subjectScore);
            Score score = Score.create(student, subject, subjectScore, semester);
            scoreRepository.save(score);
        }
    }

    private Student findStudentBy(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException("해당 학생을 찾을 수 없습니다."));
    }
}
