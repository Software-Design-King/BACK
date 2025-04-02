package INU.software_design.common.filter;

import INU.software_design.common.Constants;
import INU.software_design.common.exception.SwPlanBaseException;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.ErrorBaseCode;
import INU.software_design.common.response.code.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handleUnauthorizedException(response, e);
        }
    }

    private void handleUnauthorizedException(HttpServletResponse response, Exception e) throws IOException {
        SwPlanUseException ue = (SwPlanUseException) e;
        ErrorCode errorCode = ue.getErrorCode();
        HttpStatus httpStatus = errorCode.getHttpStatus();
        setResponse(response, httpStatus, errorCode);
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        setResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, ErrorBaseCode.INTERNAL_SERVER_ERROR);
    }

    private void setResponse(HttpServletResponse response, HttpStatus httpStatus, ErrorCode errorBaseCode) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(Constants.CHARACTER_TYPE);
        response.setStatus(httpStatus.value());
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(BaseResponse.of(errorBaseCode)));
    }
}

