package school.hei.stdgrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.stdgrade.repository.model.JGradeHistory;

public interface JGradeHistoryRepository extends JpaRepository<JGradeHistory, String> {}
