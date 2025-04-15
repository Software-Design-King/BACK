package INU.software_design.domain.auth.feign;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignProvider {
    private final KakaoApiClient kakaoApiClient;
    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoProperties kakaoProperties;

    public String getKakaoToken(final String authorizationCode,
                                final String redirectUrl
    ) {
        return kakaoAuthClient.getKakaoAccessToken(
                "authorization_code",
                kakaoProperties.getClientId(),
                redirectUrl,
                authorizationCode
        ).accessToken();
    }

    public String getKakaoTokenInfo(final String kakaoToken) {
        String header = "Bearer " + kakaoToken;
        return kakaoApiClient.getAccessTokenInfo(header).id().toString();
    }
}
