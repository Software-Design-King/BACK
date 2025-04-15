package INU.software_design.domain.attendance;

import INU.software_design.common.enums.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentId(Long studentId);

    long countByStudentIdAndType(Long studentId, AttendanceType type);

	Optional<Attendance> findByStudentIdAndDate(Long studentId, LocalDateTime date);

}
