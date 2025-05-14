package INU.software_design.domain.counsel.service;

import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.counsel.dto.request.RegisterCounselRequest;
import INU.software_design.domain.counsel.dto.response.CounselListResponse;
import INU.software_design.domain.counsel.entity.Counsel;
import INU.software_design.domain.counsel.repository.CounselRepository;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselServiceTest {

    @Mock
    private CounselRepository counselRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ClassRepository classRepository;

    @InjectMocks
    private CounselService counselService;

    private Counsel counsel1;

    private Counsel counsel2;

    @BeforeEach
    void setUp() {
        counsel1 = Counsel.create(
                1L,
                2L,
                1,
                "예시 상담",
                "성적 상담",
                "성적 향상 방안",
                List.of("테스트", "테스트2"),
                true
        );

        counsel2 = Counsel.create(
                2L,
                2L,
                1,
                "예시 상담",
                "가정 상담",
                "가정 문제 해결 방안",
                List.of("가정", "문제"),
                false
        );
    }

    @Test
    @DisplayName("상담 등록 성공 테스트")
    void testRegisterCounsel_Success() {
        // given
        Long studentId = 1L;
        RegisterCounselRequest request = new RegisterCounselRequest();
        Student mockStudent = mock(Student.class);
        Long teacherId = 2L;

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(mockStudent));
        when(classRepository.findTeacherIdByStudent(mockStudent)).thenReturn(teacherId);
        when(counselRepository.save(any(Counsel.class))).thenReturn(counsel1);

        // when
        assertDoesNotThrow(() -> counselService.registerCounsel(studentId, request));

        // then
        assertNotNull(counsel1);
        verify(studentRepository, times(1)).findById(studentId);
        verify(classRepository, times(1)).findTeacherIdByStudent(mockStudent);
        verify(counselRepository, times(1)).save(any(Counsel.class));
    }

    @Test
    @DisplayName("상담 등록 테스트 - 학생 Id 조회 실패")
    void testRegisterCounsel_InvalidStudentId() {
        // given
        Long studentId = 1L;
        RegisterCounselRequest request = new RegisterCounselRequest();

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // when
        SwPlanUseException exception = assertThrows(SwPlanUseException.class, () -> counselService.registerCounsel(studentId, request));

        // then
        assertEquals(ErrorBaseCode.BAD_REQUEST, exception.getErrorCode());
        verify(studentRepository, times(1)).findById(studentId);
        verifyNoInteractions(classRepository);
        verifyNoInteractions(counselRepository);
    }

    @Test
    @DisplayName("상담 등록 테스트 - 교사 Id 조회 실패")
    void testRegisterCounsel_MissingTeacherId() {
        // given
        Long studentId = 1L;
        RegisterCounselRequest request = new RegisterCounselRequest();
        Student mockStudent = mock(Student.class);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(mockStudent));
        when(classRepository.findTeacherIdByStudent(mockStudent)).thenThrow(EntityNotFoundException.class);

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> counselService.registerCounsel(studentId, request));

        // then
        assertNotNull(exception);
        verify(studentRepository, times(1)).findById(studentId);
        verify(classRepository, times(1)).findTeacherIdByStudent(mockStudent);
        verifyNoInteractions(counselRepository);
    }
    @Test
    @DisplayName("상담 내역 조회 성공 테스트")
    void testGetCounselList_AllCounsels() {
        // given
        Long studentId = 1L;
        int grade = 0;
        List<Counsel> counselList = List.of(counsel1, counsel2);
        when(counselRepository.findAllByStudentId(studentId)).thenReturn(counselList);

        // when
        CounselListResponse response = counselService.getCounselList(studentId, grade);

        // then
        assertNotNull(response);
        assertNull(counselList.get(0).getId());
        assertEquals(counselList.get(0).getStudentId(), 1L);
        assertEquals(counselList.get(0).getTeacherId(), 2L);
        assertEquals(response.getCounsels().get(0).getContext(), counsel1.getContent());
        assertEquals(response.getCounsels().get(0).getPlan(), counsel1.getPlan());
        assertEquals(response.getCounsels().get(0).getGrade(), counsel1.getGrade());
        assertEquals(response.getCounsels().get(0).getCreatedAt(), counsel1.getCreatedAt());
        assertEquals(response.getCounsels().get(0).getTags(), counsel1.getTags());
        assertEquals(response.getCounsels().get(0).isShared(), true);
        assertEquals(counselList.size(), response.getCounsels().size());
        verify(counselRepository, times(1)).findAllByStudentId(studentId);
    }

    @Test
    @DisplayName("상담 내역 조회 테스트 - 학년별 필터링")
    void testGetCounselList_FilteredByGrade() {
        // given
        Long studentId = 1L;
        int grade = 3;
        List<Counsel> filteredCounselList = List.of(counsel1, counsel2);
        when(counselRepository.findAllByStudentIdAndGrade(studentId, grade)).thenReturn(filteredCounselList);

        // when
        CounselListResponse response = counselService.getCounselList(studentId, grade);

        // then
        assertNotNull(response);
        assertEquals(filteredCounselList.size(), response.getCounsels().size());
        verify(counselRepository, times(1)).findAllByStudentIdAndGrade(studentId, grade);
    }

    @Test
    @DisplayName("상담 내역 조회 실패 - 학생 조회 실패")
    void testGetCounselList_FailureWithNonExistentStudent() {
        // given
        Long studentId = 999L;
        int grade = 0;
        when(counselRepository.findAllByStudentId(studentId)).thenThrow(EntityNotFoundException.class);

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> counselService.getCounselList(studentId, grade));

        // then
        assertNotNull(exception);
        verify(counselRepository, times(1)).findAllByStudentId(studentId);
    }
}