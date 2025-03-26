package INU.software_design.domain.attendance;
import INU.software_design.common.enums.AttendanceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    @Enumerated(value = EnumType.STRING)
    private AttendanceType type;

    private LocalDateTime date;

    private String reason;
}
