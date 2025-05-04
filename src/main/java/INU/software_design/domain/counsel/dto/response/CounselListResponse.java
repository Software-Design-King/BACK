package INU.software_design.domain.counsel.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CounselListResponse {
    private List<CounselInfoResponse> counsels;

    private CounselListResponse(List<CounselInfoResponse> counsels) {
        this.counsels = counsels;
    }

    public static CounselListResponse create(List<CounselInfoResponse> counsels) {
        return new CounselListResponse(counsels);
    }
}
