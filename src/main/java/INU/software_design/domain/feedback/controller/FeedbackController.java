package INU.software_design.domain.feedback.controller;

import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.feedback.dto.request.RegisterFeedRequest;
import INU.software_design.domain.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> registerFeedback(
            @PathVariable Long studentId,
            @RequestBody RegisterFeedRequest request
    ) {
        feedbackService.registerFeedback(studentId, request);
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> getFeedbackList(
            @PathVariable Long studentId,
            @RequestParam int grade
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, feedbackService.getFeedbackList(studentId, grade));
    }
}
