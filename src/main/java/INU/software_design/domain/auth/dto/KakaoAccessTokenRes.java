package INU.software_design.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoAccessTokenRes(
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") Integer expiresIn,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("refresh_token_expires_in") Integer refreshTokenExpiresIn,
        @JsonProperty("scope") String scope) {
}
