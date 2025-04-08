package INU.software_design.domain.student;

import INU.software_design.domain.student.entity.Student;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepository;

    @Transactional
    public void getStudentInfo(Long studentId, String name) {
        Student student = findById(studentId);
    }

    private Student findById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 학생을 찾을 수 없습니다."));
    }
}
