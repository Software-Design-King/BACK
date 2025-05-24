package INU.software_design.domain.attendance.controller;

import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> getAttendance(
            @PathVariable Long studentId
    ) {
        return ApiResponseUtil.success(
                SuccessCode.OK,
                attendanceService.getAttendance(studentId)
        );
    }

}
