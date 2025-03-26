package INU.software_design.domain.score.entity;
import INU.software_design.common.enums.Subject;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    @Enumerated(value = EnumType.STRING)
    private Subject subject;

    private Integer score;

    private Integer grade;

    private Integer semester;
}
