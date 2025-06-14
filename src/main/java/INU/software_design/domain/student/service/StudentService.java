package INU.software_design.domain.student.service;

import INU.software_design.common.enums.UserType;
import INU.software_design.common.exception.SwPlanUseException;
import INU.software_design.common.response.code.ErrorBaseCode;
import INU.software_design.domain.Class.ClassRepository;
import INU.software_design.domain.Class.entity.Class;
import INU.software_design.domain.auth.dto.EnrollStudentTeacherReq;
import INU.software_design.domain.student.dto.request.EnrollStudentsRequest;
import INU.software_design.domain.student.dto.request.StudentInfoRequest;
import INU.software_design.domain.student.dto.response.StudentEnrollListResponse;
import INU.software_design.domain.student.dto.response.StudentEnrollResponse;
import INU.software_design.domain.student.dto.response.StudentInfoResponse;
import INU.software_design.domain.student.dto.response.StudentListResponse;
import INU.software_design.domain.student.entity.Student;
import INU.software_design.domain.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;

    @Transactional
    public StudentListResponse getStudentList(Long teacherId) {

        Class clazz = classRepository.findByTeacherId(teacherId).orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));
        Long classId = clazz.getId();
        int classNum = clazz.getClassNumber();

        List<Student> studentList = findAllStudentBy(classId);
        List<StudentInfoResponse> students = transToStudentInfoResponse(studentList, classNum);

        return StudentListResponse.create(clazz, students);
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
    public StudentEnrollListResponse enrollStudents(Long teacherId, EnrollStudentsRequest request) {
        Class clazz = classRepository.findByTeacherId(teacherId).orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));

        List<EnrollStudentTeacherReq> students = request.getStudents();
        List<StudentEnrollResponse> enrollResponses = new ArrayList<>();
        students.forEach(student -> {
            if (isDifferentClass(student, clazz) || isNotStudent(student)) {
                throw new SwPlanUseException(ErrorBaseCode.BAD_REQUEST);
            }
            Student newStudent = Student.create(
                    clazz.getId(),
                    student.userName(),
                    student.age(),
                    student.grade(),
                    student.address(),
                    student.number(),
                    null,
                    student.gender(),
                    student.birthDate(),
                    student.contact(),
                    student.parentContact(),
                    null
            );
            enrollResponses.add(StudentEnrollResponse.create(studentRepository.save(newStudent), clazz.getClassNumber()));
        });

        return StudentEnrollListResponse.create(enrollResponses);
    }

    private int findClassNumberBy(Student student) {
        return classRepository.findClassNumberBy(student.getClassId());
    }

    private Student findById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));
    }

    private List<Student> findAllStudentBy(Long classId) {
        return studentRepository.findAllByClassId(classId)
                .orElseThrow(() -> new SwPlanUseException(ErrorBaseCode.NOT_FOUND_ENTITY));
    }

    private static List<StudentInfoResponse> transToStudentInfoResponse(List<Student> studentList, int classNum) {
        return studentList.stream()
                .map(student -> StudentInfoResponse.of(student, classNum))
                .toList();
    }

    private static boolean isNotStudent(EnrollStudentTeacherReq student) {
        return student.userType() != UserType.STUDENT;
    }

    private static boolean isDifferentClass(EnrollStudentTeacherReq student, Class clazz) {
        return student.classNum() != clazz.getClassNumber();
    }
}
