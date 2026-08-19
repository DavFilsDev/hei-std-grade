package school.hei.stdgrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.stdgrade.repository.model.JTeacherCourseAssignment;
import school.hei.stdgrade.repository.model.JTeacherCourseAssignmentId;

public interface JTeacherCourseAssignmentRepository
    extends JpaRepository<JTeacherCourseAssignment, JTeacherCourseAssignmentId> {
  // Task 2.1 (GradeService) adds the RBAC-checking finder here, e.g.:
  // boolean existsByIdTeacherIdAndIdCourseIdAndIdAcademicYearId(
  //     String teacherId, String courseId, String academicYearId);
}
