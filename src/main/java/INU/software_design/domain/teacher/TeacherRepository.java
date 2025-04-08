package INU.software_design.domain.teacher;

import INU.software_design.domain.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    Optional<Teacher> findBySocialId(String socialId);
}
