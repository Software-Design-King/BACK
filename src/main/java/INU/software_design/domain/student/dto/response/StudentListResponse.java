package INU.software_design.domain.student.dto.response;

import INU.software_design.domain.Class.entity.Class;
import INU.software_design.domain.student.entity.StudentInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StudentListResponse {

    private int grade;

    private int classNum;

    private List<StudentInfoResponse> students;

    private StudentListResponse(int grade, int classNum, List<StudentInfoResponse> students) {
        this.grade = grade;
        this.classNum = classNum;
        this.students = students;
    }

    public static StudentListResponse create(Class clazz, List<StudentInfoResponse> students) {
        return new StudentListResponse(clazz.getGrade(), clazz.getClassNumber(), students);
    }
}
