package INU.software_design.domain.counsel.controller;

import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.counsel.dto.request.RegisterCounselRequest;
import INU.software_design.domain.counsel.service.CounselService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/counsel")
public class CounselController {

    private final CounselService counselService;

    @PostMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> registerCounsel(
            @PathVariable Long studentId,
            @RequestBody RegisterCounselRequest request
    ) {
        counselService.registerCounsel(studentId, request);
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> getCounselList(
            @PathVariable Long studentId,
            @RequestParam int grade
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, counselService.getCounselList(studentId, grade));
    }
}
