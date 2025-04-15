package INU.software_design.domain.student.service;

import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.attendance.entity.Attendance;
import INU.software_design.domain.attendance.repository.AttendanceRepository;
import INU.software_design.domain.student.dto.request.AttendanceRequest;
import INU.software_design.domain.student.dto.request.StudentInfoRequest;
import INU.software_design.domain.student.dto.response.AttendanceResponse;
import INU.software_design.domain.student.dto.response.StudentInfoResponse;
import INU.software_design.domain.student.dto.response.StudentListResponse;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.student.entity.StudentInfo;
import INU.software_design.domain.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public StudentListResponse getStudentList(Long teacherId) {

        Long classId = classRepository.findIdByTeacherId(teacherId);
        int grade = classRepository.findGradeById(classId);

        List<Student> studentList = findAllStudentBy(classId);
        List<StudentInfo> students = transToStudentInfoList(studentList);

        return StudentListResponse.create(grade, students);
    }

    @Transactional
    public StudentInfoResponse getStudentInfo(Long studentId) {
        Student student = findById(studentId);
        int classNum = findClassNumberBy(student);
        return StudentInfoResponse.of(student, classNum);
    }

    @Transactional
    public StudentInfoResponse updateStudentInfo(Long studentId, StudentInfoRequest request) {
        Student student = findById(studentId);
        student.update(request);
        return StudentInfoResponse.of(student, findClassNumberBy(student));
    }

    @Transactional
    public AttendanceResponse registerAttendance(Long studentId, AttendanceRequest request) {
        Attendance attendance = Attendance.of(studentId, request);
        attendanceRepository.save(attendance);
        return AttendanceResponse.of(attendance);
    }

    @Transactional
    public AttendanceResponse updateAttendance(Long studentId, AttendanceRequest request) {
        Attendance attendance = findAttendanceBy(studentId, request);
        attendance.update(request);
        return AttendanceResponse.of(attendance);
    }

    private Attendance findAttendanceBy(Long studentId, AttendanceRequest request) {
        return attendanceRepository.findByStudentIdAndDate(studentId, request.getDate())
                .orElseThrow(() -> new EntityNotFoundException("해당 출석 정보를 찾을 수 없습니다."));
    }

    private int findClassNumberBy(Student student) {
        return classRepository.findClassNumberBy(student.getClassId());
    }

    private Student findById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 학생을 찾을 수 없습니다."));
    }

    private List<Student> findAllStudentBy(Long classId) {
        return studentRepository.findAllByClassId(classId)
                .orElseThrow(() -> new EntityNotFoundException("해당 학생을 찾을 수 없습니다."));
    }

    private static List<StudentInfo> transToStudentInfoList(List<Student> studentList) {
        return studentList.stream()
                .map(StudentInfo::create)
                .toList();
    }
}
