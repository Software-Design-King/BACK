package INU.software_design.domain.student.repository;

import INU.software_design.domain.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findBySocialId(String socialId);

    Optional<Student> findByNameAndGradeAndNumberAndClassId(String name, Integer grade, Integer number, Long classId);

    Optional<Student> findByNameAndGradeAndClassIdAndNumber(String name, Integer grade, Long classId, Integer number);

    Optional<Student> findById(Long id);

    Optional<List<Student>> findAllByClassId(Long classId);

    Optional<Student> findByEnrollCode(String enrollCode);
}
