package INU.software_design.domain.score.repository;

import INU.software_design.common.enums.Subject;
import INU.software_design.domain.score.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    @Query("SELECT SUM(s.score) FROM Score s WHERE s.semester = :semester AND s.studentId = :studentId")
    int findTotalScoreBy(int semester, Long studentId);

    // 전교생 중 학생의 순위를 구하는 쿼리 (총점 기준)
    @Query("SELECT COUNT(DISTINCT s.studentId) " +
            "FROM Score s " +
            "WHERE s.semester = :semester " +
            "AND s.studentId <> :studentId " +
            "GROUP BY s.studentId " +
            "HAVING SUM(s.score) > (SELECT SUM(sub.score) " +
            "FROM Score sub " +
            "WHERE sub.studentId = :studentId AND sub.semester = :semester)")
    Optional<Integer> findWholeRankBy(Integer semester, Long studentId);

    // 반 학생 중 학생의 순위를 구하는 쿼리 (총점 기준)
    @Query("SELECT COUNT(DISTINCT s.studentId) " +
            "FROM Score s " +
            "WHERE s.semester = :semester AND s.grade = :grade AND s.studentId <> :studentId " +
            "GROUP BY s.studentId " +
            "HAVING SUM(s.score) > (SELECT SUM(sub.score) " +
            "FROM Score sub " +
            "WHERE sub.studentId = :studentId AND sub.semester = :semester AND sub.grade = :grade)")
    Optional<Integer> findClassRankBy(Integer semester, Integer grade, Long studentId);

    List<Score> findAllByStudentIdAndSemester(Long studentId, Integer semester);

    List<Score> findAllByStudentId(Long studentId);

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

    boolean existsByStudentIdAndSubject(Long studentId, Subject subject);
}
