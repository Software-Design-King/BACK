package INU.software_design.domain.counsel.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RegisterCounselRequest {

    private String context;

    private String plan;

    private List<String> tags;

    @JsonProperty("isShared")
    private boolean isShared;
}
