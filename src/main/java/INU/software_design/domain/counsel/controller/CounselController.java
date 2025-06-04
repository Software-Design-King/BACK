package INU.software_design.domain.counsel.controller;

import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.counsel.dto.request.RegisterCounselRequest;
import INU.software_design.domain.counsel.service.CounselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/counsel")
@Tag(name = "상담", description = "상담 관련 API")
public class CounselController {

    private final CounselService counselService;

    @Operation(summary = "상담 등록", description = "학생의 상담 정보를 등록합니다.")
    @PostMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> registerCounsel(
            @Parameter(description = "학생 ID") @PathVariable Long studentId,
            @Parameter(description = "상담 등록 정보") @RequestBody RegisterCounselRequest request
    ) {
        counselService.registerCounsel(studentId, request);
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    @Operation(summary = "상담 목록 조회", description = "학생의 상담 목록을 조회합니다.")
    @GetMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> getCounselList(
            @Parameter(description = "학생 ID") @PathVariable Long studentId,
            @Parameter(description = "학년") @RequestParam int grade
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, counselService.getCounselList(studentId, grade));
    }
}
