package INU.software_design.domain.auth.dto;

import INU.software_design.common.enums.Gender;
import INU.software_design.common.enums.UserType;

import java.time.LocalDate;

public record EnrollStudentTeacherReq(
        String userName,
        int grade,
        int classNum,
        Integer number,
        UserType userType,
        Integer age,
        String kakaoToken,
        String address,
        Gender gender,
        LocalDate birthDate,
        String contact,
        String parentContact,
        String enrollCode
) {
}
