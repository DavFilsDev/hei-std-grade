package school.hei.stdgrade.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.Grade;
import school.hei.stdgrade.repository.model.JGrade;

@Component
public class JGradeMapper {
  public Grade toDomain(JGrade entity) {
    return new Grade(entity.getId(), entity.getStudentId(), entity.getExamId(), entity.getScore());
  }

  public JGrade toEntity(Grade domain) {
    JGrade entity = new JGrade();
    entity.setId(domain.id());
    entity.setStudentId(domain.studentId());
    entity.setExamId(domain.examId());
    entity.setScore(domain.score());
    return entity;
  }
}
