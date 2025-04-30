package INU.software_design.domain.student.service;

import INU.software_design.common.enums.AttendanceType;
import INU.software_design.common.enums.Gender;
import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.attendance.entity.Attendance;
import INU.software_design.domain.attendance.repository.AttendanceRepository;
import INU.software_design.domain.student.dto.request.AttendanceRequest;
import INU.software_design.domain.student.dto.request.StudentInfoRequest;
import INU.software_design.domain.student.dto.response.AttendanceResponse;
import INU.software_design.domain.student.dto.response.StudentInfoResponse;
import INU.software_design.domain.student.dto.response.StudentListResponse;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    private Attendance attendance;

    @BeforeEach
    void setUp() {
        // 레포지토리 초기화
        reset(studentRepository, classRepository, attendanceRepository);

        Long classId = 100L;
        int grade = 3;

        student = Student.create(
                classId, "홍길동", 15, grade,
                "서울시", 10, "20231234", Gender.MALE,
                LocalDate.of(2009, 5, 12), "010-1111-2222", "010-3333-4444"
        );

        attendance = Attendance.create(1L, AttendanceType.ABSENT,
                LocalDateTime.of(2025, 4, 24, 0, 0), "감기로 인한 결석", "감기");
    }

    @Test
    @DisplayName("학생 목록 조회 테스트")
    void getStudentList() {
        // Given
        Long teacherId = 1L;
        Long classId = 100L;

        when(classRepository.findIdByTeacherId(teacherId)).thenReturn(classId);
        when(classRepository.findGradeById(classId)).thenReturn(3);
        when(studentRepository.findAllByClassId(classId)).thenReturn(Optional.of(List.of(student)));

        // When
        StudentListResponse result = studentService.getStudentList(teacherId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getGrade()).isEqualTo(3);
        assertThat(result.getStudents()).hasSize(1);
        assertThat(result.getStudents().get(0).getName()).isEqualTo("홍길동");

        verify(classRepository).findIdByTeacherId(teacherId);
        verify(classRepository).findGradeById(classId);
        verify(studentRepository).findAllByClassId(classId);
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
    @DisplayName("학생 출석 입력 테스트")
    void registerAttendance() {
        // Given
        Long studentId = 1L;
        LocalDateTime date = LocalDateTime.of(2025, 4, 24, 0, 0);
        AttendanceRequest request = AttendanceRequest.builder()
                .type(AttendanceType.ABSENT)
                .date(date)
                .title("감기로 인한 결석")
                .reason("감기")
                .build();

        Attendance attendance = Attendance.of(studentId, request);

        // When
        AttendanceResponse result = studentService.registerAttendance(studentId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(AttendanceType.ABSENT);
        assertThat(result.getDate()).isEqualTo(date.toString());
        assertThat(result.getTitle()).isEqualTo("감기로 인한 결석");

        assertThat(result.getTitle()).isEqualTo(attendance.getTitle());

        verify(attendanceRepository).save(any(Attendance.class));
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("학생 출석 업데이트 테스트")
    void updateAttendance() {
        // Given
        Long studentId = 1L;
        LocalDateTime date = LocalDateTime.of(2025, 4, 24, 1, 0);
        AttendanceRequest request = AttendanceRequest.builder()
                .type(AttendanceType.LATE)
                .date(date)
                .title("감기로 인한 조퇴")
                .reason("감기")
                .build();

        when(attendanceRepository.findByStudentIdAndDate(studentId, date)).thenReturn(Optional.of(attendance));

        // When
        AttendanceResponse result = studentService.updateAttendance(studentId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(AttendanceType.LATE);
        assertThat(result.getDate()).isEqualTo(date.toString());
        assertThat(result.getTitle()).isEqualTo("감기로 인한 조퇴");

        verify(attendanceRepository).findByStudentIdAndDate(studentId, date);
        verify(attendanceRepository, times(1)).findByStudentIdAndDate(studentId, date);
    }
}