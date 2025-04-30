package INU.software_design.domain.Class;

import INU.software_design.domain.Class.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByGradeAndClassNumber(int grade, int classNumber);

    @Query("SELECT c.classNumber FROM Class c WHERE c.id = :classId")
    int findClassNumberBy(Long classId);

    @Query("SELECT c.id FROM Class c WHERE c.teacherId = :teacherId")
    Long findIdByTeacherId(Long teacherId);

    @Query("SELECT c.grade FROM Class c WHERE c.id = :classId")
    int findGradeById(Long classId);

    @Query("SELECT c FROM Class c WHERE c.teacherId = :teacherId")
    Optional<Class> findByTeacherId(Long teacherId);

    @Query("SELECT c.teacherId FROM Class c WHERE c.id = :classId")
    Long findTeacherIdById(Long classId);

}
