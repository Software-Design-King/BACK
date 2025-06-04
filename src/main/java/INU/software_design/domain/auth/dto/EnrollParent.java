package INU.software_design.domain.auth.dto;

public record EnrollParent(
        String userName,
        String studentRegisterCode,
        String kakaoToken
) {
}
