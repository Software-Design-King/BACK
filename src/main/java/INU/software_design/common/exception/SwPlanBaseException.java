package INU.software_design.common.exception;

import INU.software_design.common.response.code.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class SwPlanBaseException {
    private final ErrorCode errorCode;

    abstract HttpStatus getStatus();
}
