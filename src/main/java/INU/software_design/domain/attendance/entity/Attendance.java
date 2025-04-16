package INU.software_design.domain.attendance.entity;
import INU.software_design.common.enums.AttendanceType;
import INU.software_design.domain.student.dto.request.AttendanceRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", length = 20)
    private AttendanceType type;

    private LocalDateTime date;

    private String title;

    private String reason;

    @Builder
    private Attendance(Long studentId, AttendanceType type, LocalDateTime date, String title, String reason) {
        this.studentId = studentId;
        this.type = type;
        this.date = date;
        this.title = title;
        this.reason = reason;
    }

    public static Attendance of(Long studentId, AttendanceRequest request) {
        return Attendance.builder()
                .studentId(studentId)
                .type(request.getType())
                .date(request.getDate())
                .title(request.getTitle())
                .reason(request.getReason())
                .build();
    }

    public void update(AttendanceRequest request) {
        this.type = request.getType();
        this.date = request.getDate();
        this.title = request.getTitle();
        this.reason = request.getReason();
    }
}
