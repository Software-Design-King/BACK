package INU.software_design.domain.student.dto.response;

import INU.software_design.domain.student.entity.StudentInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StudentListResponse {

    private int grade;

    private List<StudentInfoResponse> students;

    private StudentListResponse(int grade, List<StudentInfoResponse> students) {
        this.grade = grade;
        this.students = students;
    }

    public static StudentListResponse create(int grade, List<StudentInfoResponse> students) {
        return new StudentListResponse(grade, students);
    }
}
