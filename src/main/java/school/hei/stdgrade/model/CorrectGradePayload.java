package school.hei.stdgrade.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record CorrectGradePayload(double score, String reason) {}
