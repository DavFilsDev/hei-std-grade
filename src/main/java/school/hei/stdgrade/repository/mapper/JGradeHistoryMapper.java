package school.hei.stdgrade.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.GradeHistoryEntry;
import school.hei.stdgrade.repository.model.JGradeHistory;

@Component
public class JGradeHistoryMapper {
  public GradeHistoryEntry toDomain(JGradeHistory entity) {
    return new GradeHistoryEntry(
        entity.getId(),
        entity.getGradeId(),
        entity.getOldScore(),
        entity.getNewScore(),
        entity.getReason(),
        entity.getUpdatedBy(),
        entity.getUpdatedAt());
  }

  public JGradeHistory toEntity(GradeHistoryEntry domain) {
    JGradeHistory entity = new JGradeHistory();
    entity.setId(domain.id());
    entity.setGradeId(domain.gradeId());
    entity.setOldScore(domain.oldScore());
    entity.setNewScore(domain.newScore());
    entity.setReason(domain.reason());
    entity.setUpdatedBy(domain.updatedBy());
    entity.setUpdatedAt(domain.updatedAt());
    return entity;
  }
}
