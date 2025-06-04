package INU.software_design.domain.attendance.controller;

import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.attendance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
@Tag(name = "출석", description = "출석 관련 API")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @Operation(summary = "출석 정보 조회", description = "학생의 출석 정보를 조회합니다.")
    @GetMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> getAttendance(
            @Parameter(description = "학생 ID") @PathVariable Long studentId
    ) {
        return ApiResponseUtil.success(
                SuccessCode.OK,
                attendanceService.getAttendance(studentId)
        );
    }

}
