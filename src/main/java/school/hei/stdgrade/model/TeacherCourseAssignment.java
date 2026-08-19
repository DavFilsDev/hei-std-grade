package school.hei.stdgrade.model;

import lombok.Builder;

/** Composite key (teacherId, courseId, academicYearId): several teachers can share a course. */
@Builder(toBuilder = true)
public record TeacherCourseAssignment(String teacherId, String courseId, String academicYearId) {}
