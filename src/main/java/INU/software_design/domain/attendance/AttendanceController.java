package INU.software_design.domain.attendance;

import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public ResponseEntity<BaseResponse<?>> getAttendance(
            Long studentId
    ) {
        return ApiResponseUtil.success(
                SuccessCode.OK,
                attendanceService.getAttendacne(studentId)
        );
    }

}
