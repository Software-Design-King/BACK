package INU.software_design.domain.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterFeedRequest {

    private String scoreFeed;

    private String behaviorFeed;

    private String attendanceFeed;

    private String attitudeFeed;

    private String OthersFeed;

    private boolean isPublic;
}
