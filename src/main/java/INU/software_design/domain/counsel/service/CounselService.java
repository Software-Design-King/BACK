package INU.software_design.domain.counsel.service;

import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.counsel.dto.request.RegisterCounselRequest;
import INU.software_design.domain.counsel.dto.response.CounselInfoResponse;
import INU.software_design.domain.counsel.dto.response.CounselListResponse;
import INU.software_design.domain.counsel.entity.Counsel;
import INU.software_design.domain.counsel.repository.CounselRepository;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.student.repository.StudentRepository;
import INU.software_design.domain.teacher.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CounselService {

    private final CounselRepository counselRepository;

    private final StudentRepository studentRepository;

    private final ClassRepository classRepository;

    @Transactional
    public void registerCounsel(Long studentId, RegisterCounselRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 학생을 찾을 수 없습니다."));
        Long teacherId = classRepository.findTeacherIdByStudent(student);
        Counsel counsel = Counsel.create(student, teacherId, request);
        counselRepository.save(counsel);
    }

    @Transactional
    public CounselListResponse getCounselList(Long studentId, int grade) {
        List<Counsel> counselList = (grade == 0)
                ? counselRepository.findAllByStudentId(studentId)
                : counselRepository.findAllByStudentIdAndGrade(studentId, grade);
        return CounselListResponse.create(transToFeedbackInfoList(counselList));
    }

    private static List<CounselInfoResponse> transToFeedbackInfoList(List<Counsel> counselList) {
        return counselList.stream()
                .map(CounselInfoResponse::create)
                .toList();
    }
}
