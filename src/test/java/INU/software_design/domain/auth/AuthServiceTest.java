package INU.software_design.domain.auth;

import INU.software_design.common.enums.Gender;
import INU.software_design.common.enums.UserType;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import INU.software_design.common.testutil.TestFactory;
import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.Class.entity.Class;
import INU.software_design.domain.auth.dto.EnrollStudentTeacherReq;
import INU.software_design.domain.auth.dto.EnrollStudentTeacherRes;
import INU.software_design.domain.auth.dto.UserInfoRes;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @DisplayName("학생 등록 테스트")
    void enrollStudent_Success() {
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

        when(feignProvider.getKakaoTokenInfo("kakao-token")).thenReturn("social-id");
        when(classRepository.findByGradeAndClassNumber(3, 5)).thenReturn(Optional.empty());

        // when/then
        SwPlanUseException exception = assertThrows(SwPlanUseException.class, () -> authService.enrollStudentTeacher(request));
        assertEquals(ErrorBaseCode.BAD_REQUEST, exception.getErrorCode());
    }

    @Test
    @DisplayName("교사 등록 테스트")
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
        Teacher teacher = TestFactory.create(1L, Teacher.create("김선생", "social-id"));

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
}