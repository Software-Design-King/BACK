package INU.software_design.domain.counsel.dto.response;

import INU.software_design.domain.counsel.entity.Counsel;
import INU.software_design.domain.feedback.entity.Feedback;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class CounselInfoResponse {

    private String title;

    private int grade;

    private LocalDateTime createdAt;

    private String context;

    private String plan;

    private List<String> tags;

    private boolean isShared;

    private CounselInfoResponse(String title, int grade, LocalDateTime createdAt, String context, String plan, List<String> tags, boolean isShared) {
        this.title = title;
        this.grade = grade;
        this.createdAt = createdAt;
        this.context = context;
        this.plan = plan;
        this.tags = tags;
        this.isShared = isShared;
    }

    public static CounselInfoResponse create(Counsel counsel) {
        return new CounselInfoResponse(
                counsel.getTitle(),
                counsel.getGrade(),
                counsel.getCreatedAt(),
                counsel.getContent(),
                counsel.getPlan(),
                counsel.getTags(),
                counsel.isShared()
        );
    }
}
