package INU.software_design.domain.student.entity;

import INU.software_design.common.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private Long classId;

    private String name;

    private Integer age;

    private Integer grade;

    private String address;

    private Integer number;

    @Enumerated(value = EnumType.STRING)
    private Gender gender;
}
