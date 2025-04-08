package INU.software_design.domain.auth;

import INU.software_design.common.enums.Token;
import INU.software_design.common.enums.UserType;
import INU.software_design.common.exception.KakaoException;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import INU.software_design.common.response.code.ErrorCode;
import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.Class.entity.Class;
import INU.software_design.domain.auth.dto.EnrollStudentTeacherReq;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
                    studentClass.getClassNumber(),
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
                    parentClass.getClassNumber(),
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
                    teacherClass.getClassNumber(),
                    token.getAccessToken(),
                    token.getRefreshToken()
            );
        } else {
            throw new SwPlanUseException(ErrorBaseCode.BAD_REQUEST);
        }
    }

    @Transactional
    public void enrollStudentTeacher(final EnrollStudentTeacherReq enrollStudentTeacherReq) {
        final String socialId = feignProvider.getKakaoTokenInfo(enrollStudentTeacherReq.kakaoToken());

        if(enrollStudentTeacherReq.userType() == UserType.STUDENT) {
            Class findclass = classRepository.findByGradeAndClassNumber(enrollStudentTeacherReq.grade(), enrollStudentTeacherReq.classNum()).orElseThrow(
                    () -> new SwPlanUseException(ErrorBaseCode.BAD_REQUEST)
            );
            Student newStudent = Student.create(
                    findclass.getId(),
                    enrollStudentTeacherReq.userName(),
                    enrollStudentTeacherReq.age(),
                    enrollStudentTeacherReq.grade(),
                    enrollStudentTeacherReq.address(),
                    enrollStudentTeacherReq.number(),
                    socialId,
                    enrollStudentTeacherReq.gender()
            );
            studentRepository.save(newStudent);
        } else {
            Teacher newTeacher = Teacher.create(
                    enrollStudentTeacherReq.userName(),
                    socialId
            );
            Teacher createTeacher = teacherRepository.save(newTeacher);


            // 해당 학년+반이 이미 존재하는지 확인
            Optional<Class> existingClassOpt = classRepository.findByGradeAndClassNumber(
                    enrollStudentTeacherReq.grade(),
                    enrollStudentTeacherReq.classNum()
            );

            if (existingClassOpt.isPresent()) {
                // 이미 존재하는 경우 → 해당 Class의 teacherId를 새로운 Teacher로 변경
                Class existingClass = existingClassOpt.get();
                Class updatedClass = Class.builder()
                        .id(existingClass.getId())  // id 유지
                        .grade(existingClass.getGrade())
                        .classNumber(existingClass.getClassNumber())
                        .teacherId(createTeacher.getId())  // teacherId만 변경
                        .build();
                classRepository.save(updatedClass);
            } else {
                // 존재하지 않는 경우 → 새로 생성
                Class newClass = Class.builder()
                        .grade(enrollStudentTeacherReq.grade())
                        .classNumber(enrollStudentTeacherReq.classNum())
                        .teacherId(createTeacher.getId())
                        .build();
                classRepository.save(newClass);
            }
        }
    }

    private Token getJwtToken(final long id, final UserType userType) {
        return jwtProvider.issueToken(id, userType);
    }
}
