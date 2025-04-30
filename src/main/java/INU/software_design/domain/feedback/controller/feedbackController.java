package INU.software_design.domain.feedback.controller;

import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.feedback.dto.RegisterFeedRequest;
import INU.software_design.domain.feedback.service.feedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class feedbackController {

    private final feedbackService feedbackService;

    @PostMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> registerFeedback(
            @PathVariable Long studentId,
            @RequestBody RegisterFeedRequest request
    ) {
        feedbackService.registerFeedback(studentId, request);
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
