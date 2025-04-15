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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AuthController {
    private final AuthService authService;

    //로그인
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<?>> login(
            @RequestHeader(value = "Authorization") final String authorization,
            @RequestHeader(value = "Redirecturl") final String redirectUrl
            ) {
        return ApiResponseUtil.success(
                SuccessCode.OK,
                authService.login(authorization, redirectUrl)
        );
    }

    //학생/교사 등록
    @PostMapping("/enroll/student-teacher")
    public ResponseEntity<BaseResponse<?>> enrollStudentTeacher(
            @RequestBody final EnrollStudentTeacherReq enrollStudentTeacherReq
            ) {
        return ApiResponseUtil.success(
                SuccessCode.CREATED,
                authService.enrollStudentTeacher(enrollStudentTeacherReq)
        );
    }

    //부모 등록
    @PostMapping("/enroll/parent")
    public ResponseEntity<BaseResponse<?>> enrollParent(
            @RequestBody final EnrollParent enrollParent
    ) {
        authService.enrollParent(enrollParent);
        return ApiResponseUtil.success(
                SuccessCode.CREATED
        );
    }

    //유저 정보 조회
    @GetMapping("/info")
    public ResponseEntity<BaseResponse<?>> getUserInfo(
            @UserId final Long userId,
            @UserType final INU.software_design.common.enums.UserType userType
    ) {
        return ApiResponseUtil.success(
                SuccessCode.OK,
                authService.getUserInfo(userId, userType)
        );
    }
}
