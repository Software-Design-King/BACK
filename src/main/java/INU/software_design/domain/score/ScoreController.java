package INU.software_design.domain.score;

import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/score")
public class ScoreController {
    private final ScoreService scoreService;

    @GetMapping("/{studentId}")
    public ResponseEntity<BaseResponse<?>> getScore(
            @PathVariable Long studentId,
            @RequestParam("grade") int grade,
            @RequestParam("semester") int semester
            )
    {
        return ApiResponseUtil.success(
                SuccessCode.OK,
                scoreService.getScoreDetail(studentId, grade, semester)
        );
    }
}
