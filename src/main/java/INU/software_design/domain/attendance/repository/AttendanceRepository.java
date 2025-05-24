package INU.software_design.domain.attendance.repository;

import INU.software_design.common.enums.AttendanceType;
import INU.software_design.domain.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentId(Long studentId);

    long countByStudentIdAndType(Long studentId, AttendanceType type);

	Optional<Attendance> findByStudentIdAndDate(Long studentId, LocalDateTime date);

    @Query("SELECT a FROM Attendance a WHERE a.studentId = :studentId AND a.date >= :startDateTime AND a.date < :endDateTime")
    List<Attendance> findByStudentIdAndDateBetween(
            @Param("studentId") Long studentId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

}
