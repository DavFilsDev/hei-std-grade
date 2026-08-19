package school.hei.stdgrade.model;

import java.time.Instant;
import lombok.Builder;

/**
 * {@code coefficient} is nullable: mandatory for a REGULAR exam, always null for a RETAKE (a retake
 * replaces the course average, it isn't weighted into it — see the grading rules doc).
 */
@Builder(toBuilder = true)
public record Exam(
    String id,
    Instant examDate,
    Double coefficient,
    SessionType sessionType,
    String courseId,
    String academicYearId) {}
