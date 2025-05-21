package INU.software_design.domain.score.dto.response;

import java.util.List;

public record ScoreDetailRes(
        int totalScore,
        double averageScore,
        int wholeRank, // 전교석차
        int classRank, // 반석차
        List<SubjectScore> subjects
) {
    public record SubjectScore(String subject, int score) {}
}

