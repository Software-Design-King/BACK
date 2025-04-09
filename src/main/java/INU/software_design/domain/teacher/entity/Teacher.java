package INU.software_design.domain.teacher.entity;
import INU.software_design.common.enums.Subject;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Teacher {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String socialId;

    public static Teacher create (String name, String socialId) {
        return Teacher.builder().name(name).socialId(socialId).build();
    }
}

