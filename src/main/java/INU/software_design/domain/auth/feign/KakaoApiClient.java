package INU.software_design.domain.auth.feign;

import INU.software_design.domain.auth.dto.KakaoAccessTokenInfoRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoApiClient", url = "https://kapi.kakao.com")
public interface KakaoApiClient {

    @GetMapping(value = "/v1/user/access_token_info")
    KakaoAccessTokenInfoRes getAccessTokenInfo(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String Authorization);
}
