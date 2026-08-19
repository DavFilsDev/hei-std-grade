package school.hei.stdgrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.stdgrade.repository.model.JTeacherCourseAssignment;
import school.hei.stdgrade.repository.model.JTeacherCourseAssignmentId;

public interface JTeacherCourseAssignmentRepository
    extends JpaRepository<JTeacherCourseAssignment, JTeacherCourseAssignmentId> {

  boolean existsByIdTeacherIdAndIdCourseIdAndIdAcademicYearId(
      String teacherId, String courseId, String academicYearId);
}
