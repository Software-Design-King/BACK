package INU.software_design.domain.student.controller;

import INU.software_design.common.resolver.UserId;
import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.attendance.service.AttendanceService;
import INU.software_design.domain.score.service.ScoreService;
import INU.software_design.domain.score.dto.request.StudentScoreRequest;
import INU.software_design.domain.student.service.StudentReportService;
import INU.software_design.domain.student.service.StudentService;
import INU.software_design.domain.student.dto.request.AttendanceRequest;
import INU.software_design.domain.student.dto.request.StudentInfoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StudentController {

    private final ScoreService scoreService;
    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final StudentReportService studentReportService;

    @GetMapping("/student/list")
    public ResponseEntity<BaseResponse<?>> getStudentList(
            @UserId Long teacherId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, studentService.getStudentList(teacherId));
    }

    @PostMapping("/student/score/{studentId}")
    public ResponseEntity<BaseResponse<?>> registerScore(
            @PathVariable(value = "studentId") Long studentId,
            @Valid @RequestBody StudentScoreRequest request
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, scoreService.registerStudentScore(studentId, request));
    }

    @PatchMapping("/student/score/{studentId}")
    public ResponseEntity<BaseResponse<?>> updateScore(
            @PathVariable(value = "studentId") Long studentId,
            @Valid @RequestBody StudentScoreRequest request
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, scoreService.updateStudentScore(studentId, request));
    }

    @DeleteMapping("/student/score/{studentId}")
    public ResponseEntity<BaseResponse<?>> deleteScore(
            @PathVariable(value = "studentId") Long studentId,
            @RequestParam (value = "semester") Integer semester
    ) {
        scoreService.deleteStudentScore(studentId, semester);
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    @GetMapping("/student/info/{studentId}")
    public ResponseEntity<BaseResponse<?>> getStudentInfo(
            @PathVariable(value = "studentId") Long studentId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, studentService.getStudentInfo(studentId));
    }

    @PatchMapping("/student/info/{studentId}")
    public ResponseEntity<BaseResponse<?>> updateStudentInfo(
            @PathVariable(value = "studentId") Long studentId,
            @Valid @RequestBody StudentInfoRequest request
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, studentService.updateStudentInfo(studentId, request));
    }

    @PostMapping("/student/attendance/{studentId}")
    public ResponseEntity<BaseResponse<?>> registerAttendance(
            @PathVariable(value = "studentId") Long studentId,
            @Valid @RequestBody AttendanceRequest request
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, attendanceService.registerAttendance(studentId, request));
    }

    @PatchMapping("/student/attendance/{studentId}")
    public ResponseEntity<BaseResponse<?>> updateAttendance(
            @PathVariable(value = "studentId") Long studentId,
            @Valid @RequestBody AttendanceRequest request
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, attendanceService.updateAttendance(studentId, request));
    }

    @DeleteMapping("/student/attendance/{studentId}/{date}")
    public ResponseEntity<BaseResponse<?>> deleteAttendance(
            @PathVariable(value = "studentId") Long studentId,
            @PathVariable(value = "date") LocalDate date
    ) {
        attendanceService.deleteAttendance(studentId, date);
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    @GetMapping("/student/report/{studentId}")
    public ResponseEntity<BaseResponse<?>> studentReport(
            @PathVariable(value = "studentId") Long studentId
    ) {
       return ApiResponseUtil.success(SuccessCode.OK, studentReportService.reportStudent(studentId));
    }
}
