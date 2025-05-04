package INU.software_design.domain.feedback.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FeedbackListResponse {
    private List<FeedbackInfoResponse> feedbacks;

    private FeedbackListResponse(List<FeedbackInfoResponse> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public static FeedbackListResponse create(List<FeedbackInfoResponse> feedbacks) {
        return new FeedbackListResponse(feedbacks);
    }
}
