package INU.software_design.domain.auth.jwt;

import INU.software_design.common.Constants;
import INU.software_design.common.enums.Token;
import INU.software_design.common.enums.UserType;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtProvider {
    private final JwtGenerator jwtGenerator;

    public Token issueToken(final long userId, final UserType userType) {
        return Token.of(
                generateAccessToken(userId, userType),
                generateRefreshToken(userId, userType)
        );
    }

    private String generateAccessToken(final long userId, final UserType userType) {
        return jwtGenerator.generateAccessToken(userId, userType);
    }

    public String generateRefreshToken(final long userId, final UserType userType) {
        return jwtGenerator.generateRefreshToken(userId, userType);
    }

    //RT 캐시에서 삭제
    @CacheEvict(value = Constants.REFRESH_TOKEN, key = "#userId")
    public void deleteRefreshToken(final long userId) { }

    //RT 캐시에서 조회
    @Cacheable(value = Constants.REFRESH_TOKEN, key = "#userId")
    public String findRTFromCache(final long userId) {
        throw new SwPlanUseException(ErrorBaseCode.INTERNAL_SERVER_ERROR); ///아무 값이 없으면 예외 던지기
    }

    //jwtSubject에서 userId추출
    public long getUserIdFromSubject(final String token) {
        final String subject = jwtGenerator.parseToken(token)
                .getBody()
                .getSubject();

        //subject가 숫자문자열인지 예외처리
        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new SwPlanUseException(ErrorBaseCode.INTERNAL_SERVER_ERROR); ///아무 값이 없으면 예외 던지기
        }
    }
}
