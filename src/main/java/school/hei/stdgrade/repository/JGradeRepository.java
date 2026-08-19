package school.hei.stdgrade.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.stdgrade.repository.model.JGrade;

public interface JGradeRepository extends JpaRepository<JGrade, String> {
  List<JGrade> findByStudentId(String studentId);

  List<JGrade> findByStudentIdAndExamIdIn(String studentId, List<String> examIds);
}
