package INU.software_design.domain.score.service;

import INU.software_design.common.enums.Gender;
import INU.software_design.common.enums.Subject;
import INU.software_design.common.testutil.TestStudentFactory;
import INU.software_design.domain.score.dto.ScoreDetailRes;
import INU.software_design.domain.score.dto.request.StudentScoreRequest;
import INU.software_design.domain.score.dto.response.StudentScoreResponse;
import INU.software_design.domain.score.entity.Score;
import INU.software_design.domain.score.entity.SubjectScore;
import INU.software_design.domain.score.repository.ScoreRepository;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

        student = TestStudentFactory.createWithId(1L);

        score1 = Score.create(student, Subject.MATH, 90, 1);
        score2 = Score.create(student, Subject.SCIENCE, 80, 2);
        score3 = Score.create(student, Subject.ENGLISH, 70, 3);

        subjectScore1 = SubjectScore.create(score1);
        subjectScore2 = SubjectScore.create(score2);

        scoreRequest = StudentScoreRequest.builder()
                .semester(1)
                .subjects(List.of(subjectScore1, subjectScore2))
                .build();
    }

    @Test
    @DisplayName("학생 성적 조회 테스트")
    void getScoreDetail_Success() {
        // given
        long studentId = 1L;
        int grade = 1;
        int semester = 1;

        List<Score> scores = List.of(score1, score2, score3);
        List<Object[]> allTotalScores = List.of(new Object[]{1L, 170}, new Object[]{2L, 160}, new Object[]{3L, 190});
        List<Object[]> classTotalScores = List.of(new Object[]{1L, 170}, new Object[]{2L, 160});

        when(scoreRepository.findByStudentIdAndGradeAndSemester(studentId, grade, semester)).thenReturn(scores);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(scoreRepository.findAllTotalScoresByGradeAndSemester(grade, semester)).thenReturn(allTotalScores);
        when(scoreRepository.findAllTotalScoresByClassAndSemester(grade, semester, 100L)).thenReturn(classTotalScores);

        // when
        ScoreDetailRes result = scoreService.getScoreDetail(studentId, grade, semester);

        // then
        assertNotNull(result);
        assertEquals(240, result.totalScore());
        assertEquals(80.0, result.averageScore());
        assertEquals(2, result.wholeRank());
        assertEquals(1, result.classRank());
        assertEquals(3, result.subjects().size());
        assertEquals("MATH", result.subjects().get(0).subject());
        assertEquals(90, result.subjects().get(0).score());
    }

    @Test
    @DisplayName("학생 성적 등록 테스트")
    void registerStudentScore_Success() {
        // given
        Long studentId = 1L;
        int semester = 1;

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(scoreRepository.existsByStudentIdAndSubject(1L, Subject.MATH)).thenReturn(false);
        when(scoreRepository.existsByStudentIdAndSubject(1L, Subject.SCIENCE)).thenReturn(false);

        when(scoreRepository.findTotalScoreBy(semester, studentId)).thenReturn(180);
        when(scoreRepository.findWholeRankBy(semester, studentId)).thenReturn(Optional.of(1));
        when(scoreRepository.findClassRankBy(semester, student.getGrade(), studentId)).thenReturn(Optional.of(1));
        when(scoreRepository.findAllByStudentIdAndSemester(studentId, semester))
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
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> scoreService.registerStudentScore(studentId, scoreRequest));

        assertEquals("해당 학생을 찾을 수 없습니다.", exception.getMessage());
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
        when(scoreRepository.findAllByStudentIdAndSemester(studentId, semester)).thenReturn(existingScores);
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
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> scoreService.updateStudentScore(studentId, updateRequest));

        assertEquals("해당 학생을 찾을 수 없습니다.", exception.getMessage(), "Exception message should indicate student not found");
    }

    @Test
    @DisplayName("학생 성적 업데이트 실패 - 과목 조회 실패")
    void updateStudentScore_Fail_SubjectNotFound() {
        // given
        Long studentId = 1L;
        Integer semester = 1;

        List<Score> existingScores = List.of(score1); // Only one score exists
        Score nonExistentScore = Score.create(student, Subject.KOREAN, 90, 1);
        SubjectScore nonExistentSubjectScore = SubjectScore.create(nonExistentScore);

        StudentScoreRequest updateRequest = StudentScoreRequest.builder()
                .semester(semester)
                .subjects(List.of(nonExistentSubjectScore))
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(scoreRepository.findAllByStudentIdAndSemester(studentId, semester)).thenReturn(existingScores);

        // when & then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> scoreService.updateStudentScore(studentId, updateRequest));

        assertEquals("해당 과목의 성적을 찾을 수 없습니다.", exception.getMessage(), "Exception message should indicate subject not found");
    }
}