package INU.software_design.domain.teacher;

import INU.software_design.common.response.ApiResponseUtil;
import INU.software_design.common.response.BaseResponse;
import INU.software_design.common.response.code.SuccessCode;
import INU.software_design.domain.score.ScoreService;
import INU.software_design.domain.score.dto.request.StudentScoreRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final ScoreService scoreService;

    @PostMapping("/student/score/{studentId}")
    public ResponseEntity<BaseResponse<?>> registerScore(
            @PathVariable(value = "studentId") Long studentId,
            @Valid @RequestBody StudentScoreRequest request
            ) {
        return ApiResponseUtil.success(SuccessCode.OK, scoreService.registerStudentScore(studentId, request));
    }

    @PatchMapping("/student/score/{studentId}")
    public ResponseEntity<BaseResponse<?>> updateScore(
            @PathVariable(value = "studentId") Long studentId,
            @Valid @RequestBody StudentScoreRequest request
            ) {
        return ApiResponseUtil.success(SuccessCode.OK, scoreService.updateStudentScore(studentId, request));
    }

    @DeleteMapping("/student/score/{studentId}")
    public ResponseEntity<BaseResponse<?>> deleteScore(
            @PathVariable(value = "studentId") Long studentId,
            @RequestParam (value = "semester") Integer semester
            ) {
        scoreService.deleteStudentScore(studentId, semester);
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
