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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "학생", description = "학생 관련 API")
public class StudentController {

    private final ScoreService scoreService;
    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final StudentReportService studentReportService;

    @Operation(summary = "학생 목록 조회", description = "교사가 담당하는 학생 목록을 조회합니다.")
    @GetMapping("/student/list")
    public ResponseEntity<BaseResponse<?>> getStudentList(
            @Parameter(description = "교사 ID") @UserId Long teacherId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, studentService.getStudentList(teacherId));
    }

    @Operation(summary = "성적 등록", description = "학생의 성적을 등록합니다.")
    @PostMapping("/student/score/{studentId}")
    public ResponseEntity<BaseResponse<?>> registerScore(
            @Parameter(description = "학생 ID") @PathVariable(value = "studentId") Long studentId,
            @Parameter(description = "성적 등록 정보") @Valid @RequestBody StudentScoreRequest request
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, scoreService.registerStudentScore(studentId, request));
    }

    @Operation(summary = "성적 수정", description = "학생의 성적을 수정합니다.")
    @PatchMapping("/student/score/{studentId}")
    public ResponseEntity<BaseResponse<?>> updateScore(
            @Parameter(description = "학생 ID") @PathVariable(value = "studentId") Long studentId,
            @Parameter(description = "성적 수정 정보") @Valid @RequestBody StudentScoreRequest request
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, scoreService.updateStudentScore(studentId, request));
    }

    @Operation(summary = "성적 삭제", description = "학생의 성적을 삭제합니다.")
    @DeleteMapping("/student/score/{studentId}")
    public ResponseEntity<BaseResponse<?>> deleteScore(
            @Parameter(description = "학생 ID") @PathVariable(value = "studentId") Long studentId,
            @Parameter(description = "학기") @RequestParam (value = "semester") Integer semester
    ) {
        scoreService.deleteStudentScore(studentId, semester);
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    @Operation(summary = "학생 정보 조회", description = "학생의 정보를 조회합니다.")
    @GetMapping("/student/info/{studentId}")
    public ResponseEntity<BaseResponse<?>> getStudentInfo(
            @Parameter(description = "학생 ID") @PathVariable(value = "studentId") Long studentId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, studentService.getStudentInfo(studentId));
    }

    @Operation(summary = "학생 정보 수정", description = "학생의 정보를 수정합니다.")
    @PatchMapping("/student/info/{studentId}")
    public ResponseEntity<BaseResponse<?>> updateStudentInfo(
            @Parameter(description = "학생 ID") @PathVariable(value = "studentId") Long studentId,
            @Parameter(description = "학생 정보 수정 내용") @Valid @RequestBody StudentInfoRequest request
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, studentService.updateStudentInfo(studentId, request));
    }

    @Operation(summary = "출석 등록", description = "학생의 출석 정보를 등록합니다.")
    @PostMapping("/student/attendance/{studentId}")
    public ResponseEntity<BaseResponse<?>> registerAttendance(
            @Parameter(description = "학생 ID") @PathVariable(value = "studentId") Long studentId,
            @Parameter(description = "출석 등록 정보") @Valid @RequestBody AttendanceRequest request
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, attendanceService.registerAttendance(studentId, request));
    }

    @Operation(summary = "출석 수정", description = "학생의 출석 정보를 수정합니다.")
    @PatchMapping("/student/attendance/{studentId}")
    public ResponseEntity<BaseResponse<?>> updateAttendance(
            @Parameter(description = "학생 ID") @PathVariable(value = "studentId") Long studentId,
            @Parameter(description = "출석 수정 정보") @Valid @RequestBody AttendanceRequest request
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, attendanceService.updateAttendance(studentId, request));
    }

    @Operation(summary = "출석 삭제", description = "학생의 출석 정보를 삭제합니다.")
    @DeleteMapping("/student/attendance/{studentId}/{date}")
    public ResponseEntity<BaseResponse<?>> deleteAttendance(
            @Parameter(description = "학생 ID") @PathVariable(value = "studentId") Long studentId,
            @Parameter(description = "출석 날짜") @PathVariable(value = "date") LocalDate date
    ) {
        attendanceService.deleteAttendance(studentId, date);
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    @Operation(summary = "학생 리포트 조회", description = "학생의 리포트를 조회합니다.")
    @GetMapping("/student/report/{studentId}")
    public ResponseEntity<BaseResponse<?>> studentReport(
            @Parameter(description = "학생 ID") @PathVariable(value = "studentId") Long studentId
    ) {
       return ApiResponseUtil.success(SuccessCode.OK, studentReportService.reportStudent(studentId));
    }
}
