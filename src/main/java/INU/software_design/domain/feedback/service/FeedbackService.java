package INU.software_design.domain.feedback.service;

import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.feedback.dto.request.RegisterFeedRequest;
import INU.software_design.domain.feedback.dto.response.FeedbackInfoResponse;
import INU.software_design.domain.feedback.dto.response.FeedbackListResponse;
import INU.software_design.domain.feedback.entity.Feedback;
import INU.software_design.domain.feedback.repository.FeedbackRepository;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    private final StudentRepository studentRepository;

    private final ClassRepository classRepository;

    @Transactional
    public void registerFeedback(Long studentId, RegisterFeedRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 학생을 찾을 수 없습니다."));
        Long teacherId = classRepository.findTeacherIdByStudent(student);
        Feedback feedback = Feedback.create(student, teacherId, request);
        feedbackRepository.save(feedback);
    }

    @Transactional
    public FeedbackListResponse getFeedbackList(Long studentId, int grade) {
        List<Feedback> feedbackList = (grade == 0)
                ? feedbackRepository.findAllByStudentId(studentId)
                : feedbackRepository.findAllByStudentIdAndGrade(studentId, grade);
        List<FeedbackInfoResponse> feedbacks = transToFeedbackInfoList(feedbackList);
        return FeedbackListResponse.create(feedbacks);
    }

    private static List<FeedbackInfoResponse> transToFeedbackInfoList(List<Feedback> feedbackList) {
        return feedbackList.stream()
                .map(FeedbackInfoResponse::create)
                .toList();
    }
}
