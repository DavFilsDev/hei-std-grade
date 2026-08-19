package school.hei.stdgrade.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.Course;
import school.hei.stdgrade.repository.model.JCourse;

@Component
public class JCourseMapper {
  public Course toDomain(JCourse entity) {
    return new Course(
        entity.getId(),
        entity.getRef(),
        entity.getTitle(),
        entity.getCredits(),
        entity.getTeachingUnitId(),
        entity.getTrackId());
  }

  public JCourse toEntity(Course domain) {
    JCourse entity = new JCourse();
    entity.setId(domain.id());
    entity.setRef(domain.ref());
    entity.setTitle(domain.title());
    entity.setCredits(domain.credits());
    entity.setTeachingUnitId(domain.teachingUnitId());
    entity.setTrackId(domain.trackId());
    return entity;
  }
}
