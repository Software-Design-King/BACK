package INU.software_design.domain.score.controller;

import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.score.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/score")
@Tag(name = "성적", description = "성적 관련 API")
public class ScoreController {
    private final ScoreService scoreService;

    @Operation(summary = "성적 조회", description = "학생의 성적을 조회합니다.")
    @GetMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> getScore(
            @Parameter(description = "학생 ID") @PathVariable Long studentId
    ) {
        return ApiResponseUtil.success(
                SuccessCode.OK,
                scoreService.getScore(studentId)
        );
    }
}
