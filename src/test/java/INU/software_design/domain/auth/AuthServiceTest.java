package INU.software_design.domain.auth;

import INU.software_design.common.enums.Gender;
import INU.software_design.common.enums.UserType;
import INU.software_design.common.exception.KakaoException;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import INU.software_design.common.testutil.TestFactory;
import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.Class.entity.Class;
import INU.software_design.domain.auth.dto.*;
import INU.software_design.domain.auth.feign.FeignProvider;
import INU.software_design.domain.auth.jwt.JwtProvider;
import INU.software_design.domain.parent.ParentRepository;
import INU.software_design.domain.parent.entity.Parent;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.student.repository.StudentRepository;
import INU.software_design.domain.teacher.entity.Teacher;
import INU.software_design.domain.teacher.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private FeignProvider feignProvider;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private ParentRepository parentRepository;

    @Mock
    private JwtProvider jwtProvider;

    private Student testStudent;

    private Class testClass;

    private Parent testParent;

    private Teacher testTeacher;

    private EnrollStudentTeacherReq enrollStudentTeacherReq;

    @BeforeEach
    void setUp() {
        Student student = Student.builder()
                .id(2L)
                .name("홍길동")
                .grade(3)
                .number(10)
                .classId(2L)
                .build();

        testStudent = TestFactory.create(2L, student);

        testClass = Class.builder()
                .id(2L)
                .grade(3)
                .classNumber(5)
                .build();

        testParent = Parent.builder()
                .id(1L)
                .name("김부모")
                .studentId(3L)
                .build();

        enrollStudentTeacherReq = new EnrollStudentTeacherReq(
                "홍길동",
                3,
                5,
                12,
                UserType.STUDENT,
                16,
                "kakao-token",
                "인천대학교",
                Gender.MALE,
                LocalDate.of(2005, 5, 20),
                "010-1234-5678",
                "010-9876-5432"
        );
    }

    @Test
    @DisplayName("유저 조회 테스트 - 학생")
    void getUserInfo_Student_Success() {
        // given
        Long studentId = 2L;
        UserType userType = UserType.STUDENT;

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(classRepository.findById(2L)).thenReturn(Optional.of(testClass));

        // when
        UserInfoRes userInfo = authService.getUserInfo(studentId, userType);

        // then
        assertEquals("홍길동", userInfo.name());
        assertEquals("3학년 5반", userInfo.roleInfo());
        assertEquals(10, userInfo.number());
        assertEquals(UserType.STUDENT, userInfo.userType());
    }

    @Test
    @DisplayName("유저 조회 테스트 - 학생 정보 조회 실패")
    void getUserInfo_Student_Failure() {
        // given
        Long studentId = 1L;
        UserType userType = UserType.STUDENT;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // when/then
        SwPlanUseException exception = assertThrows(SwPlanUseException.class,
                () -> authService.getUserInfo(studentId, userType),
                "SwPlanUseException은 반드시 발생해야 합니다.");
        assertEquals(ErrorBaseCode.NOT_FOUND_ENTITY, exception.getErrorCode());
    }

    @Test
    @DisplayName("유저 조회 테스트 - 학부모")
    void getUserInfo_Parent_Success() {
        // given
        Long parentId = 2L;
        UserType userType = UserType.PARENT;

        when(parentRepository.findById(parentId)).thenReturn(Optional.of(testParent));
        when(studentRepository.findById(3L)).thenReturn(Optional.of(testStudent));

        // when
        UserInfoRes userInfo = authService.getUserInfo(parentId, userType);

        // then
        assertEquals("김부모", userInfo.name());
        assertEquals("홍길동 학생의 학부모", userInfo.roleInfo());
        assertEquals(UserType.PARENT, userInfo.userType());
    }

    @Test
    @DisplayName("유저 조회 테스트 - 학부모 정보 조회 실패")
    void getUserInfo_Parent_Failure() {
        // given
        Long parentId = 3L;
        UserType userType = UserType.PARENT;

        when(parentRepository.findById(parentId)).thenReturn(Optional.empty());

        // when/then
        SwPlanUseException exception = assertThrows(SwPlanUseException.class,
                () -> authService.getUserInfo(parentId, userType),
                "SwPlanUseException은 반드시 발생해야 합니다.");
        assertEquals(ErrorBaseCode.NOT_FOUND_ENTITY, exception.getErrorCode(), "올바른 에러 코드가 반환되지 않았습니다.");
    }

    @Test
    @DisplayName("유저 조회 테스트 - 교사")
    void getUserInfo_Teacher_Success() {
        // given
        Long teacherId = 4L;
        UserType userType = UserType.TEACHER;

        Teacher teacher = Teacher.builder()
                .id(teacherId)
                .name("최선생")
                .build();

        Class teacherClass = Class.builder()
                .id(5L)
                .grade(4)
                .classNumber(2)
                .teacherId(teacherId)
                .build();

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(classRepository.findByTeacherId(teacherId)).thenReturn(Optional.of(teacherClass));

        // when
        UserInfoRes userInfo = authService.getUserInfo(teacherId, userType);

        // then
        assertEquals("최선생", userInfo.name());
        assertEquals("4학년 2반 담임", userInfo.roleInfo());
        assertEquals(UserType.TEACHER, userInfo.userType());
    }

    @Test
    @DisplayName("유저 조회 테스트 - 교사 정보 조회 실패")
    void getUserInfo_Teacher_Failure() {
        // given
        Long teacherId = 5L;
        UserType userType = UserType.TEACHER;

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        // when/then
        SwPlanUseException exception = assertThrows(SwPlanUseException.class,
                () -> authService.getUserInfo(teacherId, userType),
                "SwPlanUseException은 반드시 발생해야 합니다.");
        assertEquals(ErrorBaseCode.NOT_FOUND_ENTITY, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 성공 테스트 - 학생")
    void login_Success_Student() {
        // given
        String authorization = "valid-auth";
        String redirectUrl = "http://redirect-url.com";
        String kakaoToken = "kakao-token";
        String userSocialId = "social-id";

        Student student = Student.builder()
                .id(1L)
                .name("홍길동")
                .classId(2L)
                .build();

        Class studentClass = Class.builder()
                .id(2L)
                .grade(3)
                .classNumber(5)
                .build();

        when(feignProvider.getKakaoToken(authorization, redirectUrl)).thenReturn(kakaoToken);
        when(feignProvider.getKakaoTokenInfo(kakaoToken)).thenReturn(userSocialId);
        when(studentRepository.findBySocialId(userSocialId)).thenReturn(Optional.of(student));
        when(classRepository.findById(2L)).thenReturn(Optional.of(studentClass));
        when(jwtProvider.issueToken(1L, UserType.STUDENT))
                .thenReturn(new INU.software_design.common.enums.Token("access-token", "refresh-token"));

        // when
        LoginSuccessRes response = authService.login(authorization, redirectUrl);

        // then
        assertEquals("홍길동", response.userName());
        assertEquals(UserType.STUDENT, response.userType());
        assertEquals(3, response.grade());
        assertEquals(5, response.classNum());
        assertEquals("access-token", response.acceessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    @DisplayName("로그인 성공 테스트 - 학부모")
    void login_Success_Parent() {
        // given
        String authorization = "valid-auth";
        String redirectUrl = "http://redirect-url.com";
        String kakaoToken = "kakao-token";
        String userSocialId = "social-id";

        Parent parent = Parent.builder()
                .id(1L)
                .name("김부모")
                .studentId(2L)
                .build();

        Student student = Student.builder()
                .id(2L)
                .name("홍길동")
                .classId(3L)
                .build();

        Class parentClass = Class.builder()
                .id(3L)
                .grade(4)
                .classNumber(6)
                .build();

        when(feignProvider.getKakaoToken(authorization, redirectUrl)).thenReturn(kakaoToken);
        when(feignProvider.getKakaoTokenInfo(kakaoToken)).thenReturn(userSocialId);
        when(parentRepository.findBySocialId(userSocialId)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student));
        when(classRepository.findById(3L)).thenReturn(Optional.of(parentClass));
        when(jwtProvider.issueToken(1L, UserType.PARENT))
                .thenReturn(new INU.software_design.common.enums.Token("access-token", "refresh-token"));

        // when
        LoginSuccessRes response = authService.login(authorization, redirectUrl);

        // then
        assertEquals("김부모", response.userName());
        assertEquals(UserType.PARENT, response.userType());
        assertEquals(4, response.grade());
        assertEquals(6, response.classNum());
        assertEquals("access-token", response.acceessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    @DisplayName("로그인 성공 테스트 - 교사")
    void login_Success_Teacher() {
        // given
        String authorization = "valid-auth";
        String redirectUrl = "http://redirect-url.com";
        String kakaoToken = "kakao-token";
        String userSocialId = "social-id";

        Teacher teacher = Teacher.builder()
                .id(1L)
                .name("최선생")
                .build();

        Class teacherClass = Class.builder()
                .id(1L)
                .grade(5)
                .classNumber(1)
                .teacherId(1L)
                .build();

        when(feignProvider.getKakaoToken(authorization, redirectUrl)).thenReturn(kakaoToken);
        when(feignProvider.getKakaoTokenInfo(kakaoToken)).thenReturn(userSocialId);
        when(teacherRepository.findBySocialId(userSocialId)).thenReturn(Optional.of(teacher));
        when(classRepository.findById(1L)).thenReturn(Optional.of(teacherClass));
        when(jwtProvider.issueToken(1L, UserType.TEACHER))
                .thenReturn(new INU.software_design.common.enums.Token("access-token", "refresh-token"));

        // when
        LoginSuccessRes response = authService.login(authorization, redirectUrl);

        // then
        assertEquals("최선생", response.userName());
        assertEquals(UserType.PARENT, response.userType());
        assertEquals(5, response.grade());
        assertEquals(1, response.classNum());
        assertEquals("access-token", response.acceessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 잘못된 토큰")
    void login_Failure_InvalidToken() {
        // given
        String authorization = "invalid-auth";
        String redirectUrl = "http://redirect-url.com";

        when(feignProvider.getKakaoToken(authorization, redirectUrl))
                .thenThrow(new KakaoException("토큰 인증에 실패했습니다."));

        // when/then
        KakaoException exception = assertThrows(KakaoException.class,
                () -> authService.login(authorization, redirectUrl),
                "KakaoException은 반드시 발생해야 합니다.");
        assertEquals("토큰 인증에 실패했습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 유저를 찾을 수 없음")
    void login_Failure_UserNotFound() {
        // given
        String authorization = "valid-auth";
        String redirectUrl = "http://redirect-url.com";
        String kakaoToken = "kakao-token";
        String userSocialId = "social-id";

        when(feignProvider.getKakaoToken(authorization, redirectUrl)).thenReturn(kakaoToken);
        when(feignProvider.getKakaoTokenInfo(kakaoToken)).thenReturn(userSocialId);
        when(studentRepository.findBySocialId(userSocialId)).thenReturn(Optional.empty());
        when(parentRepository.findBySocialId(userSocialId)).thenReturn(Optional.empty());
        when(teacherRepository.findBySocialId(userSocialId)).thenReturn(Optional.empty());

        // when/then
        KakaoException exception = assertThrows(KakaoException.class,
                () -> authService.login(authorization, redirectUrl),
                "KakaoException은 반드시 발생해야 합니다.");
        assertEquals(kakaoToken, exception.getMessage());
    }

    @Test
    @DisplayName("학생 등록 테스트")
    void enrollNewStudent_Success() {
        // given
        EnrollStudentTeacherReq request = new EnrollStudentTeacherReq(
                "홍길동",
                3,
                5,
                12,
                UserType.STUDENT,
                16,
                "kakao-token",
                "인천대학교",
                Gender.MALE,
                LocalDate.of(2005, 5, 20),
                "010-1234-5678",
                "010-9876-5432"
        );

        Class testClass = Class.builder()
                .id(1L)
                .grade(3)
                .classNumber(5)
                .build();

        Student tempStudent = Student.create(
                testClass.getId(),
                request.userName(),
                request.age(),
                request.grade(),
                request.address(),
                request.number(),
                "social-id",
                request.gender(),
                request.birthDate(),
                request.contact(),
                request.parentContact()
        );
        Student student = TestFactory.create(1L, tempStudent);

        when(feignProvider.getKakaoTokenInfo("kakao-token")).thenReturn("social-id");
        when(classRepository.findByGradeAndClassNumber(3, 5)).thenReturn(Optional.of(testClass));
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(jwtProvider.issueToken(1L, UserType.STUDENT)).thenReturn(new INU.software_design.common.enums.Token("access-token", "refresh-token"));

        // when
        EnrollStudentTeacherRes response = authService.enrollStudentTeacher(request);

        // then
        assertEquals(1L, response.userId());
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    @DisplayName("학생 등록 실패 테스트 - 반 조회 실패")
    void enrollStudent_ClassNotFound() {
        // given
        when(feignProvider.getKakaoTokenInfo("kakao-token")).thenReturn("social-id");
        when(classRepository.findByGradeAndClassNumber(3, 5)).thenReturn(Optional.empty());

        // when/then
        SwPlanUseException exception = assertThrows(SwPlanUseException.class, () -> authService.enrollStudentTeacher(enrollStudentTeacherReq));
        assertEquals(ErrorBaseCode.BAD_REQUEST, exception.getErrorCode());
    }

    @Test
    @DisplayName("학생 등록 실패 테스트 - 잘못된 kakaoToken")
    void enrollStudent_InvalidKakaoToken() {
        // given
        when(feignProvider.getKakaoTokenInfo("kakao-token")).thenThrow(new KakaoException("유효하지 않은 토큰입니다."));

        // when/then
        KakaoException exception = assertThrows(KakaoException.class, () -> authService.enrollStudentTeacher(enrollStudentTeacherReq));
        assertEquals("유효하지 않은 토큰입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("학생 등록 성공 테스트 - 새로운 학생")
    void enrollNewStudent1_Success() {
        // given
        EnrollStudentTeacherReq request = new EnrollStudentTeacherReq(
                "홍길동",
                3,
                5,
                12,
                UserType.STUDENT,
                16,
                "kakao-token",
                "인천대학교",
                Gender.MALE,
                LocalDate.of(2005, 5, 20),
                "010-1234-5678",
                "010-9876-5432"
        );

        Class testClass = Class.builder()
                .id(1L)
                .grade(3)
                .classNumber(5)
                .build();

        Student tempStudent = Student.create(
                testClass.getId(),
                request.userName(),
                request.age(),
                request.grade(),
                request.address(),
                request.number(),
                "social-id",
                request.gender(),
                request.birthDate(),
                request.contact(),
                request.parentContact()
        );
        Student student = TestFactory.create(1L, tempStudent);

        when(feignProvider.getKakaoTokenInfo("kakao-token")).thenReturn("social-id");
        when(classRepository.findByGradeAndClassNumber(3, 5)).thenReturn(Optional.of(testClass));
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(jwtProvider.issueToken(1L, UserType.STUDENT)).thenReturn(new INU.software_design.common.enums.Token("access-token", "refresh-token"));

        // when
        EnrollStudentTeacherRes response = authService.enrollStudentTeacher(request);

        // then
        assertEquals(1L, response.userId());
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    @DisplayName("학생 등록 성공 테스트 - 교사 등록 학생")
    void enrollExistStudent_Success() {
        // given
        EnrollStudentTeacherReq request = new EnrollStudentTeacherReq(
                "나존재",
                3,
                5,
                100,
                UserType.STUDENT,
                16,
                "kakao-token",
                "인천대학교",
                Gender.MALE,
                LocalDate.of(2005, 5, 20),
                "010-1234-5678",
                "010-9876-5432"
        );

        Class testClass = Class.builder()
                .id(1L)
                .grade(3)
                .classNumber(5)
                .build();

        Student tempStudent = Student.create(
                testClass.getId(),
                request.userName(),
                request.age(),
                request.grade(),
                request.address(),
                request.number(),
                "social-id",
                request.gender(),
                request.birthDate(),
                request.contact(),
                request.parentContact()
        );
        Student student = TestFactory.create(1L, tempStudent);

        when(feignProvider.getKakaoTokenInfo("kakao-token")).thenReturn("social-id");
        when(classRepository.findByGradeAndClassNumber(3, 5)).thenReturn(Optional.of(testClass));
        when(studentRepository.findByNameAndGradeAndClassIdAndNumber(
                request.userName(),
                request.grade(),
                testClass.getId(),
                request.number()
        )).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(jwtProvider.issueToken(1L, UserType.STUDENT)).thenReturn(new INU.software_design.common.enums.Token("access-token", "refresh-token"));

        // when
        EnrollStudentTeacherRes response = authService.enrollStudentTeacher(request);

        // then
        assertEquals(1L, response.userId());
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    @DisplayName("교사 등록 실패 테스트 - 잘못된 classNum")
    void enrollTeacher_InvalidClassNum() {
        // given
        EnrollStudentTeacherReq request = new EnrollStudentTeacherReq(
                "TeacherName",
                4,
                0,
                3,
                UserType.TEACHER,
                null,
                "kakao-token",
                null,
                null,
                null,
                null,
                null
        );

        when(feignProvider.getKakaoTokenInfo("kakao-token")).thenReturn("social-id");

        // when/then
        NullPointerException exception = assertThrows(NullPointerException.class, () -> authService.enrollStudentTeacher(request));
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("교사 등록 성공 테스트")
    void enrollTeacher_Success() {
        // given
        EnrollStudentTeacherReq request = new EnrollStudentTeacherReq(
                "Jane Smith",
                4,
                2,
                null,
                UserType.TEACHER,
                null,
                "kakao-token",
                null,
                null,
                null,
                null,
                null
        );

        Teacher teacher = TestFactory.create(1L, Teacher.create("Jane Smith", "social-id"));

        Class mockClass = Class.builder()
                .id(1L)
                .grade(4)
                .classNumber(2)
                .teacherId(1L)
                .build();

        when(feignProvider.getKakaoTokenInfo("kakao-token")).thenReturn("social-id");
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
        when(classRepository.findByGradeAndClassNumber(4, 2)).thenReturn(Optional.of(mockClass));
        when(jwtProvider.issueToken(1L, UserType.TEACHER)).thenReturn(new INU.software_design.common.enums.Token("access-token", "refresh-token"));

        // when
        EnrollStudentTeacherRes response = authService.enrollStudentTeacher(request);

        // then
        assertEquals(1L, response.userId());
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    @DisplayName("학부모 등록 성공 테스트")
    void enrollParent_Success() {
        // given
        EnrollParent request = new EnrollParent(
                "김부모",
                "홍길동",
                3,
                5,
                12,
                "kakao-token"
        );

        Class testClass = Class.builder()
                .id(1L)
                .grade(3)
                .classNumber(5)
                .build();

        Student student = Student.builder()
                .id(2L)
                .name("홍길동")
                .grade(3)
                .number(12)
                .classId(1L)
                .build();

        when(feignProvider.getKakaoTokenInfo("kakao-token")).thenReturn("social-id");
        when(classRepository.findByGradeAndClassNumber(3, 5)).thenReturn(Optional.of(testClass));
        when(studentRepository.findByNameAndGradeAndNumberAndClassId("홍길동", 3, 12, 1L)).thenReturn(Optional.of(student));

        // when/then
        assertDoesNotThrow(() -> authService.enrollParent(request), "학부모 등록은 성공해야 합니다.");
    }

    @Test
    @DisplayName("학부모 등록 실패 테스트 - 자녀를 찾을 수 없음")
    void enrollParent_ChildNotFound() {
        // given
        EnrollParent request = new EnrollParent(
                "김부모",
                "홍길동",
                3,
                5,
                12,
                "kakao-token"
        );

        Class testClass = Class.builder()
                .id(1L)
                .grade(3)
                .classNumber(5)
                .build();

        when(feignProvider.getKakaoTokenInfo("kakao-token")).thenReturn("social-id");
        when(classRepository.findByGradeAndClassNumber(3, 5)).thenReturn(Optional.of(testClass));
        when(studentRepository.findByNameAndGradeAndNumberAndClassId("홍길동", 3, 12, 1L)).thenReturn(Optional.empty());

        // when/then
        SwPlanUseException exception = assertThrows(SwPlanUseException.class, () -> authService.enrollParent(request), "SwPlanUseException은 반드시 발생해야 합니다.");
        assertEquals(ErrorBaseCode.BAD_REQUEST, exception.getErrorCode(), "올바른 에러 코드가 반환되지 않았습니다.");
    }

    @Test
    @DisplayName("학부모 등록 실패 테스트 - 반을 찾을 수 없음")
    void enrollParent_ClassNotFound() {
        // given
        EnrollParent request = new EnrollParent(
                "김부모",
                "홍길동",
                3,
                5,
                12,
                "kakao-token"
        );

        when(feignProvider.getKakaoTokenInfo("kakao-token")).thenReturn("social-id");
        when(classRepository.findByGradeAndClassNumber(3, 5)).thenReturn(Optional.empty());

        // when/then
        SwPlanUseException exception = assertThrows(SwPlanUseException.class, () -> authService.enrollParent(request), "SwPlanUseException은 반드시 발생해야 합니다.");
        assertEquals(ErrorBaseCode.BAD_REQUEST, exception.getErrorCode(), "올바른 에러 코드가 반환되지 않았습니다.");
    }
}