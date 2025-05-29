package INU.software_design.domain.student.dto.response;

import INU.software_design.domain.student.entity.Student;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudentEnrollResponse {

    private Long studentId;

    private String name;

    private int grade;

    private int classNum;

    private int studentNum;

    private String enrollCode;

    @Builder
    private StudentEnrollResponse(Long studentId, String name, int grade, int classNum, int studentNum, String enrollCode) {
        this.studentId = studentId;
        this.name = name;
        this.grade = grade;
        this.classNum = classNum;
        this.studentNum = studentNum;
        this.enrollCode = enrollCode;
    }

    public static StudentEnrollResponse create(Student student, int classNum) {
        return StudentEnrollResponse.builder()
                .studentId(student.getId())
                .name(student.getName())
                .grade(student.getGrade())
                .classNum(classNum)
                .studentNum(student.getNumber())
                .enrollCode(student.getEnrollCode())
                .build();
    }
}
