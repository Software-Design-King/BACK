package INU.software_design.domain.feedback.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterFeedRequest {

    private String title;

    private String scoreFeed;

    private String behaviorFeed;

    private String attendanceFeed;

    private String attitudeFeed;

    private String othersFeed;

    @JsonProperty("isSharedWithStudent")
    private boolean isSharedWithStudent;

    @JsonProperty("isSharedWithParent")
    private boolean isSharedWithParent;
}
