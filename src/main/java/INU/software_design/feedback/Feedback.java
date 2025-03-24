package INU.software_design.feedback;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private Long student_id;

    private Long teacher_id;

    private Integer score;

    private String behavior;

    private String attendance;

    private String attitude;

    private String title;

    private LocalDateTime created_at;

}
