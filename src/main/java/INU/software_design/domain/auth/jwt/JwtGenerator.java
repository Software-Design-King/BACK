package INU.software_design.domain.auth.jwt;

import INU.software_design.common.Constants;
import INU.software_design.common.enums.UserType;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtGenerator {

    private final JwtProperties jwtProperties;

    //액세스 토큰 발급
    public String generateAccessToken(final long userId, final UserType userType) {
        final Date now = new Date();
        final Date expireDate = generateExpirationDate(now, true);

        final Claims claims = Jwts.claims();
        claims.put("userId", userId);                     // ✅ 명시적 claim
        claims.put("userType", userType.name());          // ✅ 명시적 claim

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setClaims(claims)
                .setExpiration(expireDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //리프레시 토큰 발급
    @CachePut(value = Constants.REFRESH_TOKEN, key = "#userId") ///없으면 추가하고, 이미 있으면 업데이트
    public String generateRefreshToken(final long userId, final UserType userType) {
        final Date now = new Date();
        final Date expireDate = generateExpirationDate(now, false);

        final Claims claims = Jwts.claims();
        claims.put("userId", userId);                     // ✅ 명시적 claim
        claims.put("userType", userType.name());          // ✅ 명시적 claim

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setClaims(claims)
                .setExpiration(expireDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Date generateExpirationDate(final Date now, final boolean isAccessToken) {
        if (isAccessToken) {
            return new Date(now.getTime() + jwtProperties.getAccessTokenExpirationTime());
        } else {
            return new Date(now.getTime() + jwtProperties.getRefreshTokenExpirationTime());
        }
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(encodeSecretKeyToBase64().getBytes());
    }

    private String encodeSecretKeyToBase64() {
        return Base64.getEncoder().encodeToString(jwtProperties.getSecret().getBytes());
    }

    public Jws<Claims> parseToken(final String token) {
        try {
            final JwtParser jwtParser = getJwtParser();
            return jwtParser.parseClaimsJws(token);
        } catch (ExpiredJwtException e) { ///만료된 jwt 예외처리
            throw new SwPlanUseException(ErrorBaseCode.BAD_REQUEST);
        } catch (UnsupportedJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) { ///잘못된 jwt 예외처리
            throw new SwPlanUseException(ErrorBaseCode.BAD_REQUEST);
        }
    }

    private JwtParser getJwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();
    }
}
