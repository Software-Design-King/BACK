package INU.software_design.domain.auth.dto;

public record EnrollParent(
        String userName,
        String childName,
        int grade,
        int classNum,
        int number,
        String kakaoToken
) {
}
