package school.hei.stdgrade.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.Exam;
import school.hei.stdgrade.model.SessionType;
import school.hei.stdgrade.repository.model.JExam;

@Component
public class JExamMapper {
  public Exam toDomain(JExam entity) {
    return new Exam(
        entity.getId(),
        entity.getExamDate(),
        entity.getCoefficient(),
        SessionType.valueOf(entity.getSessionType()),
        entity.getCourseId(),
        entity.getAcademicYearId());
  }

  public JExam toEntity(Exam domain) {
    JExam entity = new JExam();
    entity.setId(domain.id());
    entity.setExamDate(domain.examDate());
    entity.setCoefficient(domain.coefficient());
    entity.setSessionType(domain.sessionType().name());
    entity.setCourseId(domain.courseId());
    entity.setAcademicYearId(domain.academicYearId());
    return entity;
  }
}
