package INU.software_design.domain.score.service;

import INU.software_design.common.enums.ExamType;
import INU.software_design.common.enums.Subject;
import INU.software_design.common.exception.SwPlanException;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.testutil.TestFactory;
import INU.software_design.domain.score.dto.request.StudentScoreRequest;
import INU.software_design.domain.score.dto.response.SemesterScore;
import INU.software_design.domain.score.dto.response.StudentAllScoresResponse;
import INU.software_design.domain.score.dto.response.StudentScoreResponse;
import INU.software_design.domain.score.entity.Score;
import INU.software_design.domain.score.entity.SubjectScore;
import INU.software_design.domain.score.repository.ScoreRepository;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoreServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ScoreRepository scoreRepository;

    @InjectMocks
    private ScoreService scoreService;

    private Student student;

    private Score score1;
    private Score score2;
    private Score score3;

    private SubjectScore subjectScore1;
    private SubjectScore subjectScore2;

    private StudentScoreRequest scoreRequest;

    @BeforeEach
    void setUp() {
        reset(studentRepository, scoreRepository);

        student = TestFactory.createWithId(1L);

        score1 = Score.create(student, Subject.MATH, ExamType.MID, 90, 1);
        score2 = Score.create(student, Subject.SCIENCE, ExamType.MID,80, 2);
        score3 = Score.create(student, Subject.ENGLISH, ExamType.MID,70, 3);

        subjectScore1 = SubjectScore.create(score1);
        subjectScore2 = SubjectScore.create(score2);

        scoreRequest = StudentScoreRequest.builder()
                .semester(1)
                .subjects(List.of(subjectScore1, subjectScore2))
                .build();
    }

    @Test
    @DisplayName("학생 성적 등록 테스트")
    void registerStudentScore_Success() {
        // given
        Long studentId = 1L;
        int semester = 1;

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(scoreRepository.existsByStudentIdAndGradeAndSemesterAndSubjectAndExamType(1L, 1, 1, Subject.MATH, ExamType.MID)).thenReturn(false);
        when(scoreRepository.existsByStudentIdAndGradeAndSemesterAndSubjectAndExamType(1L, 1, 1, Subject.SCIENCE, ExamType.MID)).thenReturn(false);

        when(scoreRepository.findTotalScoreBy(semester, studentId)).thenReturn(180);
        when(scoreRepository.findWholeRankBy(semester, studentId)).thenReturn(Optional.of(1));
        when(scoreRepository.findClassRankBy(semester, student.getGrade(), studentId)).thenReturn(Optional.of(1));
        when(scoreRepository.findAllByStudentIdAndGradeAndSemester(studentId, 1, semester))
                .thenReturn(List.of(score1, score2));

        // when
        StudentScoreResponse response = scoreService.registerStudentScore(studentId, scoreRequest);

        // then
        assertNotNull(response);
        assertEquals(180, response.getTotalScore());
        assertEquals(1, response.getWholeRank());
        assertEquals(1, response.getClassRank());
        assertEquals(2, response.getSubjects().size());
    }

    @Test
    @DisplayName("학생 성적 등록 실패 테스트 - 학생 조회 실패")
    void registerStudentScore_Fail_StudentNotFound() {
        // given
        Long studentId = 1L;
        int semester = 1;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // when & then
        SwPlanUseException exception = assertThrows(SwPlanUseException.class,
                () -> scoreService.registerStudentScore(studentId, scoreRequest));

        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("학생 성적 등록 실패 테스트 - 이미 등록된 성적")
    void registerStudentScore_Fail_scoreIsEnrolled() {
        // given
        Long studentId = 1L;
        int semester = 1;

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(scoreRepository.existsByStudentIdAndGradeAndSemesterAndSubjectAndExamType(1L, 1, 1, Subject.MATH, ExamType.MID)).thenReturn(true);

        // when & then
        SwPlanUseException exception = assertThrows(SwPlanUseException.class,
                () -> scoreService.registerStudentScore(studentId, scoreRequest));

        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("학생 성적 업데이트 테스트")
    void updateStudentScore_Success() {
        // given
        Long studentId = 1L;
        int semester = 1;

        List<Score> existingScores = List.of(score1, score2);

        score1.updateScore(60);
        score2.updateScore(70);

        SubjectScore updatedSubjectScore1 = SubjectScore.create(score1);
        SubjectScore updatedSubjectScore2 = SubjectScore.create(score2);

        StudentScoreRequest updateRequest = StudentScoreRequest.builder()
                .semester(semester)
                .subjects(List.of(updatedSubjectScore1, updatedSubjectScore2))
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(scoreRepository.findAllByStudentIdAndGradeAndSemester(studentId, 1, semester)).thenReturn(existingScores);
        when(scoreRepository.findTotalScoreBy(semester, studentId)).thenReturn(130);
        when(scoreRepository.findWholeRankBy(semester, studentId)).thenReturn(Optional.of(1));
        when(scoreRepository.findClassRankBy(semester, student.getGrade(), studentId)).thenReturn(Optional.of(1));

        // when
        StudentScoreResponse response = scoreService.updateStudentScore(studentId, updateRequest);

        // then
        assertNotNull(response);
        assertEquals(130, response.getTotalScore());
        assertEquals(2, response.getSubjects().size());
        assertEquals(60, response.getSubjects().get(0).getScore());
        assertEquals(70, response.getSubjects().get(1).getScore());
    }

    @Test
    @DisplayName("학생 성적 업데이트 실패 테스트 - 학생 조회 실패")
    void updateStudentScore_Fail_StudentNotFound() {
        // given
        Long studentId = 1L;
        int semester = 1;

        StudentScoreRequest updateRequest = StudentScoreRequest.builder()
                .semester(semester)
                .subjects(List.of(subjectScore1))
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // when & then
        SwPlanUseException exception = assertThrows(SwPlanUseException.class,
                () -> scoreService.updateStudentScore(studentId, updateRequest));

        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("학생 성적 업데이트 실패 - 과목 조회 실패")
    void updateStudentScore_Fail_SubjectNotFound() {
        // given
        Long studentId = 1L;
        Integer semester = 1;

        List<Score> existingScores = List.of(score1); // Only one score exists
        Score nonExistentScore = Score.create(student, Subject.KOREAN, ExamType.MID,90, 1);
        SubjectScore nonExistentSubjectScore = SubjectScore.create(nonExistentScore);

        StudentScoreRequest updateRequest = StudentScoreRequest.builder()
                .semester(semester)
                .subjects(List.of(nonExistentSubjectScore))
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(scoreRepository.findAllByStudentIdAndGradeAndSemester(studentId, 1, semester)).thenReturn(existingScores);

        // when & then
        SwPlanUseException exception = assertThrows(SwPlanUseException.class,
                () -> scoreService.updateStudentScore(studentId, updateRequest));

        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("학생 성적 삭제 성공 테스트")
    void deleteStudentScore_Success() {
        // given
        Long studentId = 1L;
        Integer semester = 1;

        List<Score> existingScores = List.of(score1, score2, score3);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(scoreRepository.findAllByStudentIdAndGradeAndSemester(studentId, 1, semester)).thenReturn(existingScores);

        // when
        assertDoesNotThrow(() -> scoreService.deleteStudentScore(studentId, semester));

        // then
    }

    @Test
    @DisplayName("학생 성적 삭제 실패 테스트 - 학생 조회 실패")
    void deleteStudentScore_Fail_StudentNotFound() {
        // given
        Long studentId = 1L;
        Integer semester = 1;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // when & then
        SwPlanException exception = assertThrows(SwPlanException.class,
                () -> scoreService.deleteStudentScore(studentId, semester),
                "Exception should be thrown when student is not found");

        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("학생 성적 삭제 실패 테스트 - 학기별 성적 없음")
    void deleteStudentScore_Fail_NoScoresForSemester() {
        // given
        Long studentId = 1L;
        Integer semester = 1;

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(scoreRepository.findAllByStudentIdAndGradeAndSemester(studentId, 1, semester)).thenReturn(List.of());

        // when & then
        SwPlanException exception = assertThrows(SwPlanException.class,
                () -> scoreService.deleteStudentScore(studentId, semester));

        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("학생 성적 조회 테스트 - 성공")
    void getScore_Success() {
        // given
        Long studentId = 1L;
        Score mathScore = Score.create(student, Subject.MATH, ExamType.MID, 90, 1);
        Score scienceScore = Score.create(student, Subject.SCIENCE, ExamType.MID, 80, 1);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(scoreRepository.findAllByStudentId(studentId)).thenReturn(List.of(mathScore, scienceScore));

        // when
        StudentAllScoresResponse response = scoreService.getScore(studentId);

        // then
        assertNotNull(response);
        assertEquals(1, response.getScoresByGradeAndSemester().size());
        assertEquals(1, response.getScoresByGradeAndSemester().get(1).size());

        SemesterScore semesterScore = response.getScoresByGradeAndSemester().get(1).get(1);
        assertNotNull(semesterScore);

        assertEquals(170, semesterScore.getTotalScore());
        assertEquals(85.0, semesterScore.getAverageScore());
        assertEquals(0, semesterScore.getWholeRank());
        assertEquals(0, semesterScore.getClassRank());
        assertEquals(2, semesterScore.getSubjects().size());
    }

    @Test
    @DisplayName("학생 성적 조회 테스트 - 실패 (없는 학생)")
    void getScore_Fail_StudentNotFound() {
        // given
        Long studentId = 999L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // when & then
        SwPlanUseException exception = assertThrows(SwPlanUseException.class,
                () -> scoreService.getScore(studentId));
        assertNull(exception.getMessage());
    }
}
