package INU.software_design.domain.student.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudentInfo {

    private Long id;

    private String name;

    private StudentInfo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StudentInfo create(Student student) {
        return new StudentInfo(student.getId(), student.getName());
    }
}
