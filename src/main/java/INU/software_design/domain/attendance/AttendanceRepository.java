package INU.software_design.domain.attendance;

import INU.software_design.common.enums.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentId(Long studentId);

    long countByStudentIdAndType(Long studentId, AttendanceType type);

}
