package INU.software_design.domain.score.service;

import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import INU.software_design.domain.score.dto.response.SemesterScore;
import INU.software_design.domain.score.repository.ScoreRepository;
import INU.software_design.domain.score.entity.Score;
import INU.software_design.domain.score.entity.SubjectScore;
import INU.software_design.domain.student.repository.StudentRepository;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.score.dto.request.StudentScoreRequest;
import INU.software_design.domain.score.dto.response.StudentScoreResponse;
import INU.software_design.domain.score.dto.response.StudentAllScoresResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScoreService {

    private final StudentRepository studentRepository;

    private final ScoreRepository scoreRepository;

//	public ScoreDetailRes getScoreDetail(final long studentId, final int grade, final int semester) {
//
//        // 1. 점수 가져오기
//        List<Score> scores = scoreRepository.findByStudentIdAndGradeAndSemester(studentId, grade, semester);
//
//        int total = scores.stream().mapToInt(Score::getScore).sum();
//        double avg = total / (double) scores.size();
//
//        List<ScoreDetailRes.SubjectScore> subjectScores = scores.stream()
//                .map(s -> new ScoreDetailRes.SubjectScore(s.getSubject().name(), s.getScore()))
//                .toList();
//
//        // 2. 학생 정보 가져오기 (반 정보 필요)
//        Student student = studentRepository.findById(studentId).orElseThrow();
//
//        // 3. 전교 석차 계산
//        List<Object[]> allTotalScores = scoreRepository.findAllTotalScoresByGradeAndSemester(grade, semester);
//        int wholeRank = getRank(studentId, allTotalScores);
//
//        // 4. 반 석차 계산
//        List<Object[]> classTotalScores = scoreRepository.findAllTotalScoresByClassAndSemester(grade, semester, student.getClassId());
//        int classRank = getRank(studentId, classTotalScores);
//
//        return new ScoreDetailRes(total, avg, wholeRank, classRank, subjectScores);
//    }

    private int getRank(Long studentId, List<Object[]> scoreList) {
        List<Long> sortedStudentIds = scoreList.stream()
                .sorted((a, b) -> Long.compare(
                        ((Number) b[1]).longValue(),
                        ((Number) a[1]).longValue())
                ) // 총점 기준 내림차순 정렬
                .map(e -> ((Number) e[0]).longValue())
                .toList();

        return sortedStudentIds.indexOf(studentId) + 1;
    }

    public StudentAllScoresResponse getScore(final long studentId) {
        // 1. 학생 정보 가져오기
        Student student = findStudentBy(studentId);

        // 2. 학생의 모든 성적 가져오기
        List<Score> allScore = scoreRepository.findAllByStudentId(studentId);

        // 3. 학년과 학기별로 그룹화
        Map<Integer, Map<Integer, List<Score>>> scoresByGradeAndSemester = allScore.stream()
                .collect(Collectors.groupingBy(
                        Score::getGrade, // 1. 학년별로 그룹화
                        Collectors.groupingBy(Score::getSemester) // 2. 학년별로 그룹화 한걸 학기별로 그룹화
                ));

        // 4. 각 그룹별로 성적 정보 계산
        Map<Integer, Map<Integer, SemesterScore>> result = new HashMap<>();

        for (Map.Entry<Integer, Map<Integer, List<Score>>> scoresByGrade : scoresByGradeAndSemester.entrySet()) {
            // 학년 추출
            Integer grade = scoresByGrade.getKey();
            // 해당 학년 학기별 성적들
            Map<Integer, List<Score>> semesterScores = scoresByGrade.getValue();

            Map<Integer, SemesterScore> semesterResult = new HashMap<>();

            for (Map.Entry<Integer, List<Score>> scoresBySemester : semesterScores.entrySet()) {
                // 학기 추출
                Integer semester = scoresBySemester.getKey();
                // 해당 학기의 성적
                List<Score> scores = scoresBySemester.getValue();

                // 총점
                int total = scores.stream().mapToInt(Score::getScore).sum();
                // 평균
                double avg = total / (double) scores.size();

                // 과목별 점수 리스트 생성
                List<SubjectScore> subjectScores = scores.stream()
                        .map(SubjectScore::create)
                        .toList();

                // 전교 석차 계산
                List<Object[]> allTotalScores = scoreRepository.findAllTotalScoresByGradeAndSemester(grade, semester);
                int wholeRank = getRank(studentId, allTotalScores);

                // 반 석차 계산
                List<Object[]> classTotalScores = scoreRepository.findAllTotalScoresByClassAndSemester(grade, semester, student.getClassId());
                int classRank = getRank(studentId, classTotalScores);

                // 학기별 성적 정보 생성
                SemesterScore semesterScore = SemesterScore.create(total, avg, wholeRank, classRank, subjectScores);

                semesterResult.put(semester, semesterScore);
            }

            result.put(grade, semesterResult);
        }

        return StudentAllScoresResponse.create(result);
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
                    .orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));
            Score.updateScore(subjectScore.getScore());
            Score.updateExamType(subjectScore.getExamType());
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
            throw new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY);
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
                throw new SwPlanUseException(ErrorBaseCode.BAD_REQUEST);
            }
            Score score = Score.create(student, subjectScore.getName(), subjectScore, semester);
            scoreRepository.save(score);
        }
    }

    private boolean isEnrolled(Student student, SubjectScore subjectScore) {
        return scoreRepository.existsByStudentIdAndSubject(student.getId(), subjectScore.getName());
    }

    private Student findStudentBy(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));
    }
}
