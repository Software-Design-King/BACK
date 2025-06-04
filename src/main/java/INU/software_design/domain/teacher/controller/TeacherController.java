package INU.software_design.domain.teacher.controller;

import INU.software_design.common.resolver.UserId;
import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.student.dto.request.EnrollStudentsRequest;
import INU.software_design.domain.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
@Tag(name = "교사", description = "교사 관련 API")
public class TeacherController {

    private final StudentService studentService;

    @Operation(summary = "학생 등록", description = "교사가 담당하는 학생들을 등록합니다.")
    @PostMapping("/enroll/students")
    public ResponseEntity<BaseResponse<?>> enrollStudents(
            @Parameter(description = "교사 ID") @UserId final Long userId,
            @Parameter(description = "학생 등록 정보") @RequestBody final EnrollStudentsRequest request
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, studentService.enrollStudents(userId, request));
    }
}