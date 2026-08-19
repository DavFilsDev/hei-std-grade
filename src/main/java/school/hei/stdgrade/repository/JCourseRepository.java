package school.hei.stdgrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.stdgrade.repository.model.JCourse;

public interface JCourseRepository extends JpaRepository<JCourse, String> {}
