package INU.software_design.common.enums;

import INU.software_design.domain.score.entity.SubjectScore;

public enum Subject {
    KOREAN,
    ENGLISH,
    MATH,
    SCIENCE,
    SOCIAL,
    PHYSICAL_EDUCATION;

    public static Subject from(SubjectScore request) {
        String name = request.getName();
        try {
            return Subject.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 과목명 : " + name);
        }
    }
}
