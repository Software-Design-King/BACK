package INU.software_design.domain.student.entity;

import INU.software_design.common.enums.Gender;
import INU.software_design.domain.student.dto.request.StudentInfoRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    private String socialId;

    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    private LocalDate birthDate;

    private String contact;

    private String parentContact;

    public static Student create(
                         final Long classId,
                         String name,
                         Integer age,
                         Integer grade,
                         String address,
                         Integer number,
                         String socialId,
                         Gender gender
                         ) {
        return Student.builder().classId(classId).name(name).age(age).grade(grade).address(address).number(number).socialId(socialId).gender(gender).build();
    }

    public void update(StudentInfoRequest request) {
        this.name = request.getName();
        this.age = request.getAge();
        this.grade = request.getGrade();
        this.address = request.getAddress();
        this.number = request.getNumber();
        this.gender = request.getGender();
        this.birthDate = request.getBirthDate();
        this.contact = request.getContact();
        this.parentContact = request.getParentContact();
    }
}
