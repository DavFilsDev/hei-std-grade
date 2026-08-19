package school.hei.stdgrade.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.stdgrade.repository.model.JExam;

public interface JExamRepository extends JpaRepository<JExam, String> {
  List<JExam> findByCourseIdAndAcademicYearId(String courseId, String academicYearId);

  List<JExam> findByCourseId(String courseId);
}
