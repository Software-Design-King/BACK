package INU.software_design.domain.student.dto.response;

import INU.software_design.common.enums.Gender;
import INU.software_design.domain.student.entity.Student;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudentInfoResponse {

    private Long studentId;

    private String name;

    private Integer age;

    private String birthDate;

    private Gender gender;

    private String address;

    private String contact;

    private String entranceDate;

    private int grade;

    private int classNum;

    private int studentNum;

    private String enrollCode;

    @Builder
    private StudentInfoResponse(Long studentId, String name, Integer age, String birthDate, Gender gender, String address,
                               String contact, String entranceDate, int grade, int classNum, int studentNum, String enrollCode) {
        this.studentId = studentId;
        this.name = name;
        this.age = age;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.contact = contact;
        this.entranceDate = entranceDate;
        this.grade = grade;
        this.classNum = classNum;
        this.studentNum = studentNum;
        this.enrollCode = enrollCode;
    }

    public static StudentInfoResponse of(Student student, Integer classNum) {
        return StudentInfoResponse.builder()
                .studentId(student.getId())
                .name(student.getName())
                .age(student.getAge())
                .birthDate(student.getBirthDate().toString())
                .gender(student.getGender())
                .address(student.getAddress())
                .contact(student.getContact())
                .entranceDate(student.getBirthDate().toString())
                .grade(student.getGrade())
                .classNum(classNum)
                .studentNum(student.getNumber())
                .enrollCode(student.getEnrollCode())
                .build();
    }
}
