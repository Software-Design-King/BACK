package INU.software_design.domain.auth;

import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.auth.dto.LoginSuccessRes;
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
            @RequestHeader(value = "RedirectUrl") final String redirectUrl
            ) {
        return ApiResponseUtil.success(
                SuccessCode.OK,
                authService.login(authorization, redirectUrl)
        );
    }
}
