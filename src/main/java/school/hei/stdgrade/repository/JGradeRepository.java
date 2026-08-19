package school.hei.stdgrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.stdgrade.repository.model.JGrade;

public interface JGradeRepository extends JpaRepository<JGrade, String> {}
