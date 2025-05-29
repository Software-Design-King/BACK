package INU.software_design.domain.student.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StudentEnrollListResponse {
    private List<StudentEnrollResponse> studentEnrollResponses;

    private StudentEnrollListResponse(List<StudentEnrollResponse> studentEnrollResponses) {
        this.studentEnrollResponses = studentEnrollResponses;
    }

    public static StudentEnrollListResponse create(List<StudentEnrollResponse> enrollResponses) {
        return new StudentEnrollListResponse(enrollResponses);
    }
}
