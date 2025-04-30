package INU.software_design.common.testutil;

import INU.software_design.domain.student.entity.Student;
import INU.software_design.common.enums.Gender;

import java.lang.reflect.Field;
import java.time.LocalDate;

public class TestStudentFactory {

    public static Student createWithId(Long id) {
        Student student = Student.create(
                100L,                     // classId
                "홍길동",                 // name
                15,                      // age
                1,                       // grade
                "서울시",                // address
                10,                      // number
                "20231234",              // socialId
                Gender.MALE,             // gender
                LocalDate.of(2009, 5, 12), // birthDate
                "010-1234-5678",         // contact
                "010-8765-4321"          // parentContact
        );
        setPrivateId(student, id);
        return student;
    }

    private static void setPrivateId(Student student, Long id) {
        try {
            Field idField = Student.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(student, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Student ID 설정 실패", e);
        }
    }
}