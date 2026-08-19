package school.hei.stdgrade.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record TranscriptSummary(
    String studentId,
    String academicYearId,
    TranscriptStatus status,
    double average,
    int creditsEarned) {}
