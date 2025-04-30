package INU.software_design.domain.feedback.service;

import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.feedback.dto.RegisterFeedRequest;
import INU.software_design.domain.feedback.entity.Feedback;
import INU.software_design.domain.feedback.repository.FeedbackRepository;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.student.repository.StudentRepository;
import INU.software_design.domain.teacher.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class feedbackService {

    private final FeedbackRepository feedbackRepository;

    private final StudentRepository studentRepository;

    private final ClassRepository classRepository;

    @Transactional
    public void registerFeedback(Long studentId, RegisterFeedRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 학생을 찾을 수 없습니다."));

        Long teacherId = classRepository.findTeacherIdById(student.getClassId());
        Feedback feedback = Feedback.create(studentId, teacherId, request);
        feedbackRepository.save(feedback);
    }
}
