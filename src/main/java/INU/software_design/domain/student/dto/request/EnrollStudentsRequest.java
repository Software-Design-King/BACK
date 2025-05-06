package INU.software_design.domain.student.dto.request;

import INU.software_design.domain.auth.dto.EnrollStudentTeacherReq;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class EnrollStudentsRequest {
    List<EnrollStudentTeacherReq> students;
}
