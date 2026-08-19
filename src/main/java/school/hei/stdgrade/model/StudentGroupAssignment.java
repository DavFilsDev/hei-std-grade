package school.hei.stdgrade.model;

import lombok.Builder;

/** Composite key (studentId, academicYearId): a student has exactly one group per school year. */
@Builder(toBuilder = true)
public record StudentGroupAssignment(String studentId, String groupId, String academicYearId) {}
