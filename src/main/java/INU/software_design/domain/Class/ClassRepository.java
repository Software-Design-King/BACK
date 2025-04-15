package INU.software_design.domain.Class;

import INU.software_design.domain.Class.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByGradeAndClassNumber(int grade, int classNumber);

    @Query("SELECT c.classNumber FROM Class c WHERE c.id = :classId")
    int findClassNumberBy(Long classId);

    Long findIdByTeacherId(Long teacherId);

    int findGradeById(Long classId);

    Optional<Class> findByTeacherId(Long teacherId);


}
