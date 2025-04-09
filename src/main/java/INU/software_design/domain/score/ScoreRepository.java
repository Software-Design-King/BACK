package INU.software_design.domain.score;

import INU.software_design.domain.score.entity.Score;
import INU.software_design.domain.score.entity.SubjectScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    int findWholeRankBy(Integer semester, Long studentId);

    @Query("SELECT COUNT(DISTINCT s.studentId) " +
            "FROM Score s " +
            "WHERE s.semester = :semester AND s.grade = :grade AND s.studentId <> :studentId " +
            "GROUP BY s.studentId " +
            "HAVING SUM(s.score) > (SELECT SUM(sub.score) " +
            "FROM Score sub " +
            "WHERE sub.studentId = :studentId AND sub.semester = :semester AND sub.grade = :grade)")
    int findClassRankBy(Integer semester, Integer grade, Long studentId);

    List<Score> findAllByStudentIdAndSemester(Long studentId, Integer semester);
}