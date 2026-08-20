package school.hei.stdgrade.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.stdgrade.repository.model.JAcademicTrack;

public interface JAcademicTrackRepository extends JpaRepository<JAcademicTrack, String> {
  Optional<JAcademicTrack> findByCode(String code);

  boolean existsByCodeAndIdNot(String code, String id);
}
