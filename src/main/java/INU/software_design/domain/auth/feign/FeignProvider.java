package INU.software_design.domain.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignProvider {
    private final KakaoApiClient kakaoApiClient;

    public getKakaoToken(final String authorizationCode, final String redirectUrl) {

    }
}
