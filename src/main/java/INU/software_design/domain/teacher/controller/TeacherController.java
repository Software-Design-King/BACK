package INU.software_design.domain.teacher.controller;

import INU.software_design.common.resolver.UserId;
import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.student.dto.request.EnrollStudentsRequest;
import INU.software_design.domain.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final StudentService studentService;

    @PostMapping("/enroll/students")
    public ResponseEntity<BaseResponse<?>> enrollStudents(
            @UserId final Long userId,
            @RequestBody final EnrollStudentsRequest request
    ) {
        studentService.enrollStudents(userId, request);
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}