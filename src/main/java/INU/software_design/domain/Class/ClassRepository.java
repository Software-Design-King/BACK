package INU.software_design.domain.Class;

import INU.software_design.domain.Class.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClassRepository extends JpaRepository<Class, Long> {
    @Query("SELECT c.class_number FROM Class c WHERE c.id = :classId")
    int findClassNumberBy(Long classId);

    int findGradeByTeacherId(Long teacherId);
}
