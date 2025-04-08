package INU.software_design.domain.Class;

import INU.software_design.domain.Class.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByGradeAndClassNumber(int grade, int classNumber);

}
