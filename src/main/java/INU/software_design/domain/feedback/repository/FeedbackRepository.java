package INU.software_design.domain.feedback.repository;

import INU.software_design.domain.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findAllByStudentId(Long studentId);
    List<Feedback> findAllByStudentIdAndGrade(Long studentId, int grade);
}
