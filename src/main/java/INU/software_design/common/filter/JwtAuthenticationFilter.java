package INU.software_design.common.filter;

import INU.software_design.common.Constants;
import INU.software_design.common.TokenAuthentication;
import INU.software_design.common.enums.UserType;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import INU.software_design.domain.auth.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static INU.software_design.common.TokenAuthentication.createTokenAuthentication;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String accessToken = getAccessToken(request);

        // userId 및 userType 추출
        final long userId = jwtProvider.getUserIdFromClaims(accessToken);
        final UserType userType = jwtProvider.getUserType(accessToken);

        // 인증 객체 생성
        doAuthentication(accessToken, userId, userType);
        filterChain.doFilter(request, response);
    }

    private String getAccessToken(final HttpServletRequest request) {
        final String accessToken = request.getHeader(Constants.AUTHORIZATION);
        if (StringUtils.hasText(accessToken) && accessToken.startsWith(Constants.BEARER)) {
            return accessToken.substring(Constants.BEARER.length());
        }
        throw new SwPlanUseException(ErrorBaseCode.UNAUTHORIZED);
    }

    private void doAuthentication(final String token, final long userId, final UserType userType) {
        TokenAuthentication tokenAuthentication = createTokenAuthentication(token, userId, userType);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(tokenAuthentication);
    }
}
