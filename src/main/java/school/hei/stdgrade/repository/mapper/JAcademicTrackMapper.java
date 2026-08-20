package school.hei.stdgrade.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.AcademicTrack;
import school.hei.stdgrade.repository.model.JAcademicTrack;

@Component
public class JAcademicTrackMapper {

  public AcademicTrack toDomain(JAcademicTrack entity) {
    return new AcademicTrack(entity.getId(), entity.getCode(), entity.getName());
  }

  public JAcademicTrack toEntity(AcademicTrack domain) {
    JAcademicTrack entity = new JAcademicTrack();
    entity.setId(domain.id());
    entity.setCode(domain.code());
    entity.setName(domain.name());
    return entity;
  }
}
