package INU.software_design.domain.Class.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
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

    private long teacherId;

    private int grade;

    private int classNumber;
}
