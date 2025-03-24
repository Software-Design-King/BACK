package INU.software_design.student.domain;

import INU.software_design.common.enums.Gender;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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

    private Long class_id;

    private String name;

    private Integer age;

    private Integer grade;

    private String address;

    private Integer number;

    private Gender gender;
}
