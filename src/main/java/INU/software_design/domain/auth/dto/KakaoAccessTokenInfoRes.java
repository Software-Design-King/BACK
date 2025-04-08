package INU.software_design.domain.auth.dto;

public record KakaoAccessTokenRes(
        Long id,
        Integer expires_in,
        Integer app_id
) {

}
