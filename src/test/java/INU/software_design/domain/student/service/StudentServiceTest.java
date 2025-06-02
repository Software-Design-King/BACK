package INU.software_design.domain.student.service;

import INU.software_design.common.enums.Gender;
import INU.software_design.common.enums.UserType;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.Class.entity.Class;
import INU.software_design.domain.attendance.repository.AttendanceRepository;
import INU.software_design.domain.auth.dto.EnrollStudentTeacherReq;
import INU.software_design.domain.student.dto.request.EnrollStudentsRequest;
import INU.software_design.domain.student.dto.request.StudentInfoRequest;
import INU.software_design.domain.student.dto.response.StudentEnrollListResponse;
import INU.software_design.domain.student.dto.response.StudentInfoResponse;
import INU.software_design.domain.student.dto.response.StudentListResponse;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.student.repository.StudentRepository;
import INU.software_design.domain.teacher.entity.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student;

    private Student student2;

    private Teacher teacher;

    EnrollStudentTeacherReq enrollStudentTeacherReq1;

    EnrollStudentTeacherReq enrollStudentTeacherReq2;

    @BeforeEach
    void setUp() {
        // 레포지토리 초기화
        reset(studentRepository, classRepository, attendanceRepository);

        Long classId = 100L;
        int grade = 3;
        student = Student.create(
                classId, "홍길동", 15, grade,
                "서울시", 10, "20231234", Gender.MALE,
                LocalDate.of(2009, 5, 12), "010-1111-2222", "010-3333-4444", "11112222"
        );

        student2 = Student.create(
                classId, "김이나", 15, grade,
                "서울시", 10, "20231235", Gender.FEMALE,
                LocalDate.of(2009, 5, 13), "010-1111-3333", "010-3333-5555", "11113333"
        );

        teacher = Teacher.builder()
                .id(1L)
                .name("김교사")
                .socialId("11223344")
                .build();

        enrollStudentTeacherReq1 = new EnrollStudentTeacherReq("김철수", 1, 3, 10, UserType.STUDENT, 15, "kakaoToken", "서울", Gender.MALE,
                LocalDate.of(2008, 6, 10), "010-1111-1111", "010-2222-2222", "11115555");

        enrollStudentTeacherReq2 = new EnrollStudentTeacherReq("김하나", 1, 3, 11, UserType.STUDENT, 15, "kakaoToken2", "서울", Gender.FEMALE,
                LocalDate.of(2008, 6, 10), "010-1111-1111", "010-2222-2222", "11114444");
    }



    @Test
    @DisplayName("학생 정보 조회 테스트")
    void getStudentInfo() {
        // Given
        Long studentId = 1L;
        int classNum = 100;

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(classRepository.findClassNumberBy(student.getClassId())).thenReturn(classNum);

        // When
        StudentInfoResponse result = studentService.getStudentInfo(studentId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getClassNum()).isEqualTo(100);

        verify(studentRepository).findById(studentId);
        verify(studentRepository, times(1)).findById(studentId);
        verify(classRepository).findClassNumberBy(student.getClassId());
        verify(classRepository, times(1)).findClassNumberBy(student.getClassId());
    }

    @Test
    @DisplayName("학생 정보 업데이트 테스트")
    void updateStudentInfo() {
        // Given
        StudentInfoRequest request = StudentInfoRequest.builder()
                .name("김범수") // 수정
                .age(16) // 수정
                .grade(3)
                .number(10)
                .address("서울시")
                .gender(Gender.MALE)
                .contact("010-1111-2222")
                .parentContact("010-3333-4444")
                .birthDate(LocalDate.of(2009, 5, 12))
                .build();

        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

        // when
        StudentInfoResponse result = studentService.updateStudentInfo(student.getId(), request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("김범수");
        assertThat(result.getAge()).isEqualTo(16);

        verify(studentRepository, times(1)).findById(student.getId());
    }

    @Test
    @DisplayName("학생 등록 실패 테스트 - 클래스 불일치")
    void enrollStudents_Failure() {
        // Given
        Long teacherId = 1L;
        Class clazz = mock(Class.class);
        EnrollStudentsRequest request = EnrollStudentsRequest.create(List.of(
                new EnrollStudentTeacherReq("김철수", 1, 3, 9, UserType.STUDENT, 15, "kakaoToken", "서울", Gender.MALE,
                        LocalDate.of(2008, 6, 10), "010-1111-1111", "010-2222-2222", "11112222")
        ));

        when(classRepository.findByTeacherId(teacherId)).thenReturn(Optional.of(clazz));
        when(clazz.getClassNumber()).thenReturn(10);

        // When
        Throwable exception = catchThrowable(() -> studentService.enrollStudents(teacherId, request));

        // Then
        assertThat(exception).isInstanceOf(SwPlanUseException.class);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("학생 등록 실패 테스트 - 학생이 아님")
    void enrollStudents_Failure_isNotStudent() {
        // Given
        Long teacherId = 1L;
        Class clazz = mock(Class.class);
        EnrollStudentsRequest request = EnrollStudentsRequest.create(List.of(
                new EnrollStudentTeacherReq("김철수", 1, 3, 10, UserType.TEACHER, 15, "kakaoToken", "서울", Gender.MALE,
                        LocalDate.of(2008, 6, 10), "010-1111-1111", "010-2222-2222", "11112222")
        ));

        // When
        Throwable exception = catchThrowable(() -> studentService.enrollStudents(teacherId, request));

        // Then
        assertThat(exception).isInstanceOf(SwPlanUseException.class);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("학생 리스트 조회 테스트 - 성공")
    void getStudentList_Success() {
        // Given
        Long teacherId = 1L;
        Class clazz = mock(Class.class);
        when(classRepository.findByTeacherId(teacherId)).thenReturn(Optional.of(clazz));
        when(clazz.getId()).thenReturn(100L);
        when(clazz.getClassNumber()).thenReturn(10);
        when(studentRepository.findAllByClassId(100L)).thenReturn(Optional.of(List.of(student, student2)));

        // When
        StudentListResponse response = studentService.getStudentList(teacherId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getClassNum()).isEqualTo(10);
        assertThat(response.getStudents()).hasSize(2);
        verify(classRepository, times(1)).findByTeacherId(teacherId);
        verify(studentRepository, times(1)).findAllByClassId(100L);
    }

    @Test
    @DisplayName("학생 리스트 조회 테스트 - 실패: 존재하지 않는 교사")
    void getStudentList_Failure_TeacherNotFound() {
        // Given
        Long teacherId = 1L;
        when(classRepository.findByTeacherId(teacherId)).thenReturn(Optional.empty());

        // When
        Throwable exception = catchThrowable(() -> studentService.getStudentList(teacherId));

        // Then
        assertThat(exception).isInstanceOf(SwPlanUseException.class);
        verify(classRepository, times(1)).findByTeacherId(teacherId);
        verify(studentRepository, never()).findAllByClassId(any());
    }

    @Test
    @DisplayName("학생 등록 테스트 - 성공")
    void enrollStudents_Success() {
        // Given
        Long teacherId = 1L;
        Class clazz = mock(Class.class);
        when(classRepository.findByTeacherId(teacherId)).thenReturn(Optional.of(clazz));
        when(clazz.getClassNumber()).thenReturn(3);
        when(clazz.getId()).thenReturn(100L);

        EnrollStudentsRequest request = EnrollStudentsRequest.create(List.of(
                new EnrollStudentTeacherReq("김철수", 1, 3, 10, UserType.STUDENT, 15, "kakaoToken", "서울", Gender.MALE,
                        LocalDate.of(2008, 6, 10), "010-1111-1111", "010-2222-2222", "11112222")
        ));


        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        StudentEnrollListResponse response = studentService.enrollStudents(teacherId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStudentEnrollResponses()).hasSize(1);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("학생 등록 실패 테스트 - 클래스 불일치")
    void enrollStudents_Failure_DifferentClass() {
        // Given
        Long teacherId = 1L;
        Class clazz = mock(Class.class);
        when(classRepository.findByTeacherId(teacherId)).thenReturn(Optional.of(clazz));
        when(clazz.getClassNumber()).thenReturn(10);

        EnrollStudentsRequest request = EnrollStudentsRequest.create(List.of(
                new EnrollStudentTeacherReq("김철수", 1, 3, 9, UserType.STUDENT, 15, "kakaoToken", "서울", Gender.MALE,
                        LocalDate.of(2008, 6, 10), "010-1111-1111", "010-2222-2222", "11112222")
        ));

        // When
        Throwable exception = catchThrowable(() -> studentService.enrollStudents(teacherId, request));

        // Then
        assertThat(exception).isInstanceOf(SwPlanUseException.class);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("학생 등록 실패 테스트 - 학생이 아님")
    void enrollStudents_Failure_NotStudent() {
        // Given
        Long teacherId = 1L;
        Class clazz = mock(Class.class);
        when(classRepository.findByTeacherId(teacherId)).thenReturn(Optional.of(clazz));
        when(clazz.getClassNumber()).thenReturn(10);

        EnrollStudentsRequest request = EnrollStudentsRequest.create(List.of(
                new EnrollStudentTeacherReq("김철수", 1, 3, 10, UserType.TEACHER, 15, "kakaoToken", "서울", Gender.MALE,
                        LocalDate.of(2008, 6, 10), "010-1111-1111", "010-2222-2222", "11112222")
        ));

        // When
        Throwable exception = catchThrowable(() -> studentService.enrollStudents(teacherId, request));

        // Then
        assertThat(exception).isInstanceOf(SwPlanUseException.class);
        verify(studentRepository, never()).save(any(Student.class));
    }
}