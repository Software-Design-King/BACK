package INU.software_design.domain.auth.dto;

import INU.software_design.common.enums.UserType;
import lombok.Builder;

@Builder
public record LoginSuccessRes(
        String userName,
        UserType userType,
        int grade,
        int classNum,
        String acceessToken,
        String refreshToken
) {
    public static LoginSuccessRes create(String userName, UserType userType, int grade, int classNum, String acceessToken, String refreshToken) {
        return LoginSuccessRes
                .builder()
                .userName(userName)
                .userType(userType)
                .grade(grade)
                .classNum(classNum)
                .acceessToken(acceessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
