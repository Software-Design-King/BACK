package INU.software_design.domain.score.controller;

import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.score.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/score")
public class ScoreController {
    private final ScoreService scoreService;

    @GetMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> getScore(@PathVariable Long studentId)
    {
        return ApiResponseUtil.success(
                SuccessCode.OK,
                scoreService.getScore(studentId)
        );
    }

}
