package INU.software_design.domain.auth.feign;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignProvider {
    private final KakaoApiClient kakaoApiClient;
    private final KakaoAuthClient kakaoAuthClient;

    public String getKakaoToken(final String authorizationCode,
                                final String redirectUrl
    ) {
        return kakaoAuthClient.getKakaoAccessToken(
                "authorization_code",
                "5e2053f8ae5352d5fd4d7f5a8a311ce7",
                redirectUrl,
                authorizationCode
        ).accessToken();
    }

    public String getKakaoTokenInfo(final String kakaoToken) {
        return kakaoApiClient.getAccessTokenInfo(kakaoToken).id().toString();
    }



}
