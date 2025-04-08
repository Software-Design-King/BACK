package INU.software_design.domain.Class.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        name = "class",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"grade", "classNumber"})
        }
)
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long teacherId;

    private Integer grade;

    private Integer classNumber;
}
