package INU.software_design.domain.auth;

import INU.software_design.common.enums.Token;
import INU.software_design.common.enums.UserType;
import INU.software_design.common.exception.KakaoException;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import INU.software_design.common.response.code.ErrorCode;
import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.Class.entity.Class;
import INU.software_design.domain.auth.dto.*;
import INU.software_design.domain.auth.feign.FeignProvider;
import INU.software_design.domain.auth.jwt.JwtProperties;
import INU.software_design.domain.auth.jwt.JwtProvider;
import INU.software_design.domain.parent.ParentRepository;
import INU.software_design.domain.parent.entity.Parent;
import INU.software_design.domain.student.repository.StudentRepository;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.teacher.repository.TeacherRepository;
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
                    () -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY)
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
                    () -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY)
            );
            Class parentClass = classRepository.findById(student.getClassId()).orElseThrow(
                    () -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY)
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
            Class teacherClass = classRepository.findByTeacherId(((Teacher) user).getId()).orElseThrow(
                    () -> new SwPlanUseException(ErrorBaseCode.CONFLICT)
            );
            return LoginSuccessRes.create(
                    ((Teacher) user).getName(),
                    UserType.TEACHER,
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
    public EnrollStudentTeacherRes enrollStudentTeacher(final EnrollStudentTeacherReq enrollStudentTeacherReq) {
        final String socialId = getSocialId(enrollStudentTeacherReq.kakaoToken());

        if(enrollStudentTeacherReq.userType() == UserType.STUDENT) {
            Student student = studentRepository.findByEnrollCode(enrollStudentTeacherReq.enrollCode())
                    .orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));

            student.updateSocialId(socialId);
            Student savedStudent = studentRepository.save(student);

            Token token = getJwtToken(savedStudent.getId(), UserType.STUDENT);
            return EnrollStudentTeacherRes.of(savedStudent.getId(), token.getAccessToken(), token.getRefreshToken());

        } else {
            Teacher newTeacher = Teacher.create(
                    enrollStudentTeacherReq.userName(),
                    socialId
            );
            Teacher savedTeacher = teacherRepository.save(newTeacher);

            Token token = getJwtToken((savedTeacher.getId()), UserType.TEACHER);

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
                        .teacherId(savedTeacher.getId())  // teacherId만 변경
                        .build();
                classRepository.save(updatedClass);
            } else {
                // 존재하지 않는 경우 → 새로 생성
                Class newClass = Class.builder()
                        .grade(enrollStudentTeacherReq.grade())
                        .classNumber(enrollStudentTeacherReq.classNum())
                        .teacherId(savedTeacher.getId())
                        .build();
                classRepository.save(newClass);
            }

            return EnrollStudentTeacherRes.of(savedTeacher.getId(), token.getAccessToken(), token.getRefreshToken());
        }
    }

    //부모 등록
    @Transactional
    public void enrollParent(final EnrollParent enrollParent) {
        String socialId = getSocialId(enrollParent.kakaoToken());

        // 2. Student 정보 조회
        Student student = studentRepository.findByEnrollCode(enrollParent.studentRegisterCode()).orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.BAD_REQUEST));

        // 3. Parent 엔티티 생성 및 저장
        Parent parent = Parent.builder()
                .name(enrollParent.userName())
                .studentId(student.getId())
                .socialId(socialId)
                .build();
        parentRepository.save(parent);
    }

    //유저 정보 조회
    public UserInfoRes getUserInfo(Long userId, UserType userType) {
        return switch (userType) {
            case STUDENT -> getStudentInfo(userId);
            case PARENT -> getParentInfo(userId);
            case TEACHER -> getTeacherInfo(userId);
        };
    }

    private UserInfoRes getStudentInfo(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));

        int classNumber = getClassNumber(student.getClassId());

        return UserInfoRes.of(
                student.getName(),
                student.getGrade() + "학년 " + classNumber + "반",
                student.getNumber(), // 이게 학생 번호
                UserType.STUDENT,
                student.getId()
        );
    }

    private UserInfoRes getParentInfo(Long parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));

        Student child = studentRepository.findById(parent.getStudentId())
                .orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));

        return UserInfoRes.of(
                parent.getName(),
                child.getName() + " 학생의 학부모",
                UserType.PARENT,
                parent.getId(), // 부모 아이디
                parent.getStudentId() // 학생 아이디
        );
    }

    private UserInfoRes getTeacherInfo(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));

        Class clazz = classRepository.findByTeacherId(teacherId)
                .orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));

        return UserInfoRes.of(
                teacher.getName(),
                clazz.getGrade() + "학년 " + clazz.getClassNumber() + "반 담임",
                UserType.TEACHER,
                teacher.getId()
        );
    }

    private int getClassNumber(Long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY))
                .getClassNumber();
    }

    private Token getJwtToken(final long id, final UserType userType) {
        return jwtProvider.issueToken(id, userType);
    }

    private String getSocialId(final String kakaoToken) {
        return feignProvider.getKakaoTokenInfo(kakaoToken);
    }
}
