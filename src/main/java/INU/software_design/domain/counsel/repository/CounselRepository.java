package INU.software_design.domain.counsel.repository;

import INU.software_design.domain.counsel.entity.Counsel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CounselRepository extends JpaRepository<Counsel, Long> {
    List<Counsel> findAllByStudentId(Long studentId);

    List<Counsel> findAllByStudentIdAndGrade(Long studentId, int grade);
}
