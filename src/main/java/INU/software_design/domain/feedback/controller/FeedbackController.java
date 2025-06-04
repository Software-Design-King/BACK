package INU.software_design.domain.feedback.controller;

import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.feedback.dto.request.RegisterFeedRequest;
import INU.software_design.domain.feedback.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
@Tag(name = "피드백", description = "피드백 관련 API")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "피드백 등록", description = "학생의 피드백을 등록합니다.")
    @PostMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> registerFeedback(
            @Parameter(description = "학생 ID") @PathVariable Long studentId,
            @Parameter(description = "피드백 등록 정보") @RequestBody RegisterFeedRequest request
    ) {
        feedbackService.registerFeedback(studentId, request);
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    @Operation(summary = "피드백 목록 조회", description = "학생의 피드백 목록을 조회합니다.")
    @GetMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> getFeedbackList(
            @Parameter(description = "학생 ID") @PathVariable Long studentId,
            @Parameter(description = "학년") @RequestParam int grade
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, feedbackService.getFeedbackList(studentId, grade));
    }
}
