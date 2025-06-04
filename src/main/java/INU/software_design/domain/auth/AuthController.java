package INU.software_design.domain.auth;

import INU.software_design.common.resolver.UserId;
import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.common.usertype.UserType;
import INU.software_design.domain.auth.dto.EnrollParent;
import INU.software_design.domain.auth.dto.EnrollStudentTeacherReq;
import INU.software_design.domain.auth.dto.LoginSuccessRes;
import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "인증", description = "인증 관련 API")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인", description = "카카오 로그인을 수행합니다.")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<?>> login(
            @Parameter(description = "카카오 인증 토큰") @RequestHeader(value = "Authorization") final String authorization,
            @Parameter(description = "리다이렉트 URL") @RequestHeader(value = "Redirecturl") final String redirectUrl
            ) {
        return ApiResponseUtil.success(
                SuccessCode.OK,
                authService.login(authorization, redirectUrl)
        );
    }

    @Operation(summary = "학생/교사 등록", description = "학생 또는 교사 계정을 등록합니다.")
    @PostMapping("/enroll/student-teacher")
    public ResponseEntity<BaseResponse<?>> enrollStudentTeacher(
            @Parameter(description = "학생/교사 등록 정보") @RequestBody final EnrollStudentTeacherReq enrollStudentTeacherReq
            ) {
        return ApiResponseUtil.success(
                SuccessCode.CREATED,
                authService.enrollStudentTeacher(enrollStudentTeacherReq)
        );
    }

    @Operation(summary = "부모 등록", description = "부모 계정을 등록합니다.")
    @PostMapping("/enroll/parent")
    public ResponseEntity<BaseResponse<?>> enrollParent(
            @Parameter(description = "부모 등록 정보") @RequestBody final EnrollParent enrollParent
    ) {
        authService.enrollParent(enrollParent);
        return ApiResponseUtil.success(
                SuccessCode.CREATED
        );
    }

    @Operation(summary = "유저 정보 조회", description = "현재 로그인한 유저의 정보를 조회합니다.")
    @GetMapping("/info")
    public ResponseEntity<BaseResponse<?>> getUserInfo(
            @Parameter(description = "유저 ID") @UserId final Long userId,
            @Parameter(description = "유저 타입") @UserType final INU.software_design.common.enums.UserType userType
    ) {
        return ApiResponseUtil.success(
                SuccessCode.OK,
                authService.getUserInfo(userId, userType)
        );
    }
}
