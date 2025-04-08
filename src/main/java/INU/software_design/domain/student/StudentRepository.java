package INU.software_design.domain.student;

import INU.software_design.domain.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findBySocialId(String socialId);

}
