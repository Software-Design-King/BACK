package INU.software_design.domain.auth.dto;

import lombok.Builder;

@Builder
public record EnrollStudentTeacherRes(
        Long userId,
        String accessToken,
        String refreshToken
) {
    public static EnrollStudentTeacherRes of(final Long userId, final String accessToken, final String refreshToken) {
        return EnrollStudentTeacherRes.builder().userId(userId).accessToken(accessToken).refreshToken(refreshToken).build();
    }

}
