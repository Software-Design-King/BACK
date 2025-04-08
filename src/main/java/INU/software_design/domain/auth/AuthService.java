package INU.software_design.domain.auth;

import INU.software_design.common.enums.Token;
import INU.software_design.common.enums.UserType;
import INU.software_design.common.exception.KakaoException;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import INU.software_design.common.response.code.ErrorCode;
import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.Class.entity.Class;
import INU.software_design.domain.auth.dto.LoginSuccessRes;
import INU.software_design.domain.auth.feign.FeignProvider;
import INU.software_design.domain.auth.jwt.JwtProperties;
import INU.software_design.domain.auth.jwt.JwtProvider;
import INU.software_design.domain.parent.ParentRepository;
import INU.software_design.domain.parent.entity.Parent;
import INU.software_design.domain.student.StudentRepository;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.teacher.TeacherRepository;
import INU.software_design.domain.teacher.entity.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final FeignProvider feignProvider;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassRepository classRepository;
    private final JwtProvider jwtProvider;

    public LoginSuccessRes login(String authorization, String redirectUrl) {
        final String kakaoToken = feignProvider.getKakaoToken(authorization, redirectUrl);
        final String userSocialId = feignProvider.getKakaoTokenInfo(kakaoToken);

        Object user = studentRepository.findBySocialId(userSocialId)
                .map(student -> (Object) student)
                .or(() -> parentRepository.findBySocialId(userSocialId).map(parent -> (Object) parent))
                .or(() -> teacherRepository.findBySocialId(userSocialId).map(teacher -> (Object) teacher))
                .orElseThrow(() -> new KakaoException(kakaoToken));

        if (user instanceof Student) {
            Token token = getJwtToken(((Student) user).getId(), UserType.STUDENT);
            Class studentClass = classRepository.findById(((Student) user).getClassId()).orElseThrow(
                    () -> new SwPlanUseException(ErrorBaseCode.BAD_REQUEST)
            );
            return LoginSuccessRes.create(
                    ((Student) user).getName(),
                    UserType.STUDENT,
                    studentClass.getGrade(),
                    studentClass.getClass_number(),
                    token.getAccessToken(),
                    token.getRefreshToken()
                    );
        } else if (user instanceof Parent) {
            Token token = getJwtToken(((Parent) user).getId(), UserType.PARENT);
            Student student = studentRepository.findById(((Parent) user).getStudentId()).orElseThrow(
                    () -> new SwPlanUseException(ErrorBaseCode.BAD_REQUEST)
            );
            Class parentClass = classRepository.findById(student.getClassId()).orElseThrow(
                    () -> new SwPlanUseException(ErrorBaseCode.BAD_REQUEST)
            );
            return LoginSuccessRes.create(
                    ((Parent) user).getName(),
                    UserType.PARENT,
                    parentClass.getGrade(),
                    parentClass.getClass_number(),
                    token.getAccessToken(),
                    token.getRefreshToken()
            );
        } else if (user instanceof Teacher) {
            Token token = getJwtToken(((Teacher) user).getId(), UserType.TEACHER);
            Class teacherClass = classRepository.findById(((Teacher) user).getId()).orElseThrow(
                    () -> new SwPlanUseException(ErrorBaseCode.BAD_REQUEST)
            );
            return LoginSuccessRes.create(
                    ((Teacher) user).getName(),
                    UserType.PARENT,
                    teacherClass.getGrade(),
                    teacherClass.getClass_number(),
                    token.getAccessToken(),
                    token.getRefreshToken()
            );
        } else {
            throw new SwPlanUseException(ErrorBaseCode.BAD_REQUEST);
        }
    }

    private Token getJwtToken(final long id, final UserType userType) {
        return jwtProvider.issueToken(id, userType);
    }
}
