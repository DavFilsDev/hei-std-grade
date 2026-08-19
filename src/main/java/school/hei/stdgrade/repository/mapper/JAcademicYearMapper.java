package school.hei.stdgrade.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.AcademicYear;
import school.hei.stdgrade.repository.model.JAcademicYear;

@Component
public class JAcademicYearMapper {
  public AcademicYear toDomain(JAcademicYear entity) {
    return new AcademicYear(entity.getId(), entity.getLabel(), entity.getStartYear());
  }

  public JAcademicYear toEntity(AcademicYear domain) {
    JAcademicYear entity = new JAcademicYear();
    entity.setId(domain.id());
    entity.setLabel(domain.label());
    entity.setStartYear(domain.startYear());
    return entity;
  }
}
