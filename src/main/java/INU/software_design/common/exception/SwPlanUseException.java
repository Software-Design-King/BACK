package INU.software_design.common.exception;

import INU.software_design.common.response.code.ErrorCode;
import org.springframework.http.HttpStatus;

public class SwPlanUseException extends SwPlanBaseException{
    public SwPlanUseException(final ErrorCode errorCode) {
        super(errorCode);
    }

    @Override
    HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
