package INU.software_design.domain.student.dto.request;

import INU.software_design.common.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class StudentInfoRequest {

    private String name;

    private Integer age;

    private Integer grade;

    private String address;

    private Integer number;

    private Gender gender;

    private LocalDate birthDate;

    private String contact;

    private String parentContact;
}
