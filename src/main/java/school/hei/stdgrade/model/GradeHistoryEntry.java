package school.hei.stdgrade.model;

import java.time.Instant;
import lombok.Builder;

/** Append-only audit trail: written by the service on every {@code grade} update, never edited. */
@Builder(toBuilder = true)
public record GradeHistoryEntry(
    String id,
    String gradeId,
    double oldScore,
    double newScore,
    String reason,
    String updatedBy,
    Instant updatedAt) {}
