package INU.software_design.domain.student.dto.response;

import INU.software_design.common.enums.Gender;
import INU.software_design.domain.student.entity.Student;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudentInfoResponse {

    private String name;

    private String birthDate;

    private Gender gender;

    private String address;

    private String contact;

    private String entranceDate;

    private String schoolName;

    private int grade;

    private int classNum;

    private int studentNum;

    @Builder
    private StudentInfoResponse(String name, String birthDate, Gender gender, String address,
                               String contact, String entranceDate, String schoolName,
                               int grade, int classNum, int studentNum) {
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.contact = contact;
        this.entranceDate = entranceDate;
        this.schoolName = schoolName;
        this.grade = grade;
        this.classNum = classNum;
        this.studentNum = studentNum;
    }

    public static StudentInfoResponse of(Student student, Integer classNum) {
        // TODO : schoolName 처리
        return StudentInfoResponse.builder()
                .name(student.getName())
                .birthDate(student.getBirthDate().toString())
                .gender(student.getGender())
                .address(student.getAddress())
                .contact(student.getContact())
                .entranceDate(student.getBirthDate().toString())
                .grade(student.getGrade())
                .classNum(classNum)
                .studentNum(student.getNumber())
                .build();
    }
}
