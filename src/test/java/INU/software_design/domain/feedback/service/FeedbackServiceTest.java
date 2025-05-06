package INU.software_design.domain.feedback.service;

import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.feedback.controller.FeedbackController;
import INU.software_design.domain.feedback.dto.request.RegisterFeedRequest;
import INU.software_design.domain.feedback.dto.response.FeedbackInfoResponse;
import INU.software_design.domain.feedback.dto.response.FeedbackListResponse;
import INU.software_design.domain.feedback.entity.Feedback;
import INU.software_design.domain.feedback.repository.FeedbackRepository;
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
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ClassRepository classRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    private RegisterFeedRequest registerFeedRequest;

    private Feedback feedback1;

    private Feedback feedback2;

    @BeforeEach
    void setUp() {
        registerFeedRequest = RegisterFeedRequest.builder()
                .scoreFeed("성적 피드백")
                .behaviorFeed("행동 피드백")
                .attendanceFeed("출결 피드백")
                .attitudeFeed("태도 피드백")
                .othersFeed("기타 피드백")
                .isSharedWithStudent(true)
                .isSharedWithParent(false)
                .build();

        feedback1 = Feedback.create(
                1L, 1L, 3,
                "성적 피드백",
                "행동 피드백",
                "출석 피드백",
                "태도 피드백",
                "기타 피드백",
                true,
                true
        );

        feedback2 = Feedback.create(
                1L, 2L, 3,
                "성적 피드백",
                "행동 피드백",
                "출석 피드백",
                "태도 피드백",
                "기타 피드백",
                true,
                true
        );
    }

    @Test
    @DisplayName("피드백 등록 성공 테스트")
    void registerFeedback_Success() {
        // given
        Long studentId = 1L;
        Long teacherId = 2L;

        Student student = mock(Student.class);
        Feedback savedMock = mock(Feedback.class);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(classRepository.findTeacherIdByStudent(student)).thenReturn(teacherId);
        when(student.getId()).thenReturn(studentId);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(savedMock);

        // when & then
        assertDoesNotThrow(() -> feedbackService.registerFeedback(studentId, registerFeedRequest));

        verify(studentRepository, times(1)).findById(studentId);
        verify(classRepository, times(1)).findTeacherIdByStudent(student);
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    @DisplayName("피드백 등록 테스트 - 학생 조회 실패")
    void registerFeedback_Failure_StudentNotFound() {
        // given
        Long studentId = 1L;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // when
        SwPlanUseException exception = assertThrows(SwPlanUseException.class,
                () -> feedbackService.registerFeedback(studentId, registerFeedRequest));

        // then
        assertNotNull(exception);
        assertEquals(ErrorBaseCode.BAD_REQUEST, exception.getErrorCode());
        verify(studentRepository, times(1)).findById(studentId);
        verifyNoInteractions(classRepository);
        verifyNoInteractions(feedbackRepository);
    }
    @Test
    @DisplayName("피드백 조회 성공 - 전체 학년")
    void getFeedbackList_Success_AllGrades() {
        // given
        Long studentId = 1L;

        when(feedbackRepository.findAllByStudentId(studentId)).thenReturn(List.of(feedback1, feedback2));

        // when
        FeedbackListResponse response = feedbackService.getFeedbackList(studentId, 0);

        // then
        assertNotNull(response);
        assertEquals(response.getFeedbacks().get(0).getAttendanceFeed(), feedback1.getAttendance());
        assertEquals(response.getFeedbacks().get(0).getBehaviorFeed(), feedback1.getBehavior());
        assertEquals(response.getFeedbacks().get(0).getGrade(), feedback1.getGrade());
        assertEquals(response.getFeedbacks().get(0).getOthersFeed(), feedback1.getOthers());
        assertEquals(response.getFeedbacks().get(0).getScoreFeed(), feedback1.getScore());
        assertEquals(response.getFeedbacks().get(0).getCreatedAt(), feedback1.getCreatedAt());
        assertEquals(response.getFeedbacks().get(0).getAttitudeFeed(), feedback1.getAttitude());
        assertEquals(response.getFeedbacks().get(0).isSharedWithParent(), feedback1.getIsSharedWithParent());
        assertEquals(response.getFeedbacks().get(0).isSharedWithStudent(), feedback1.getIsSharedWithStudent());
        assertEquals(1L, feedback1.getStudentId());
        assertEquals(1L, feedback1.getTeacherId());
        assertNull(feedback1.getId());
        assertEquals(2, response.getFeedbacks().size());
        verify(feedbackRepository, times(1)).findAllByStudentId(studentId);
    }

    @Test
    @DisplayName("피드백 조회 성공 - 특정 학년")
    void getFeedbackList_Success_SpecificGrade() {
        // given
        Long studentId = 1L;
        int grade = 3;
        Feedback feedback = mock(Feedback.class);

        when(feedbackRepository.findAllByStudentIdAndGrade(studentId, grade)).thenReturn(List.of(feedback));

        // when
        FeedbackListResponse response = feedbackService.getFeedbackList(studentId, grade);

        // then
        assertNotNull(response);
        assertEquals(1, response.getFeedbacks().size());
        verify(feedbackRepository, times(1)).findAllByStudentIdAndGrade(studentId, grade);
    }

    @Test
    @DisplayName("피드백 리스트 조회 테스트 - 학생 조회 실패")
    void getFeedbackList_Failure_StudentNotFound() {
        // given
        Long studentId = 1L;

        when(feedbackRepository.findAllByStudentId(studentId)).thenThrow(new EntityNotFoundException());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> feedbackService.getFeedbackList(studentId, 0));

        // then
        assertNotNull(exception);
        verify(feedbackRepository, times(1)).findAllByStudentId(studentId);
    }
}