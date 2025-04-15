package INU.software_design.domain.score;

import INU.software_design.domain.score.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findByStudentIdAndGradeAndSemester(Long studentId, int grade, int semester);

    @Query("SELECT s.studentId, SUM(s.score) as totalScore " +
            "FROM Score s " +
            "WHERE s.grade = :grade AND s.semester = :semester " +
            "GROUP BY s.studentId")
    List<Object[]> findAllTotalScoresByGradeAndSemester(@Param("grade") int grade, @Param("semester") int semester);

    @Query("SELECT sc.studentId, SUM(sc.score) as totalScore " +
            "FROM Score sc " +
            "JOIN Student st ON sc.studentId = st.id " +
            "WHERE sc.grade = :grade AND sc.semester = :semester AND st.classId = :classId " +
            "GROUP BY sc.studentId")
    List<Object[]> findAllTotalScoresByClassAndSemester(@Param("grade") int grade, @Param("semester") int semester, @Param("classId") Long classId);

}
