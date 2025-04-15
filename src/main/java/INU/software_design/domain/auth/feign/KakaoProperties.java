package INU.software_design.domain.auth.feign;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {
    private String clientId;

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
