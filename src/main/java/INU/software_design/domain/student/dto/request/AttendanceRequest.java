package INU.software_design.domain.student.dto.request;

import INU.software_design.common.enums.AttendanceType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AttendanceRequest {

    private AttendanceType type;

    private LocalDateTime date;

    private String title;

    private String reason;
}
