package INU.software_design.domain.auth.dto;

import INU.software_design.common.enums.UserType;

public record UserInfoRes(
        String name,
        String roleInfo,
        Integer number, // 학생일 경우에만 값이 있음, 나머지는 null
        UserType userType,
        Long userId
) {
    public static UserInfoRes of(String name, String roleInfo, UserType userType, Long userId) {
        return new UserInfoRes(name, roleInfo, null, userType, userId);
    }

    public static UserInfoRes of(String name, String roleInfo, Integer number, UserType userType, Long userId) {
        return new UserInfoRes(name, roleInfo, number, userType, userId);
    }
}
