package INU.software_design.domain.score.service;

import INU.software_design.domain.score.repository.ScoreRepository;
import INU.software_design.domain.score.entity.Score;
import INU.software_design.domain.score.entity.SubjectScore;
import INU.software_design.domain.student.repository.StudentRepository;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.score.dto.request.StudentScoreRequest;
import INU.software_design.domain.score.dto.response.StudentScoreResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import INU.software_design.domain.score.dto.ScoreDetailRes;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScoreService {

    private final StudentRepository studentRepository;
    private final ScoreRepository scoreRepository;

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
            Score Score = StudentScores.stream()
                    .filter(score -> score.getSubject().equals(subjectScore.getName()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("해당 과목의 성적을 찾을 수 없습니다."));
            Score.updateScore(subjectScore.getScore());
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
        return scoreRepository.findClassRankBy(semester, student.getGrade(), studentId).orElse(1);
    }

    private Integer getWholeRankBy(Long studentId, Integer semester) {
        return scoreRepository.findWholeRankBy(semester, studentId).orElse(1);
    }

    private Integer getTotalScore(Long studentId, Integer semester) {
        return scoreRepository.findTotalScoreBy(semester, studentId);
    }

    private void saveStudentScores(StudentScoreRequest request, Student student, Integer semester) {
        for (SubjectScore subjectScore : request.getSubjects()) {
            if (isEnrolled(student, subjectScore)) {
                throw new IllegalArgumentException("이미 등록된 과목입니다.");
            }
            Score score = Score.create(student, subjectScore.getName(), subjectScore, semester);
            scoreRepository.save(score);
        }
    }

    private boolean isEnrolled(Student student, SubjectScore subjectScore) {
        return scoreRepository.existsByStudentIdAndSubject(student.getId(), subjectScore.getName());
    }

    private Student findStudentBy(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException("해당 학생을 찾을 수 없습니다."));
    }
}
