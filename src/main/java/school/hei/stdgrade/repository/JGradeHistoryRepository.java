package school.hei.stdgrade.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.stdgrade.repository.model.JGradeHistory;

public interface JGradeHistoryRepository extends JpaRepository<JGradeHistory, String> {
  List<JGradeHistory> findByGradeId(String gradeId);
}
