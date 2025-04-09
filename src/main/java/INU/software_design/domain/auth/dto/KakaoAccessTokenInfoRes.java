package INU.software_design.domain.auth.dto;

public record KakaoAccessTokenInfoRes(
        Long id,
        Integer expires_in,
        Integer app_id
) {

}
