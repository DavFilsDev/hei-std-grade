package school.hei.stdgrade.service;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import school.hei.stdgrade.model.Exam;
import school.hei.stdgrade.repository.JExamRepository;
import school.hei.stdgrade.repository.JTeacherCourseAssignmentRepository;
import school.hei.stdgrade.repository.mapper.JExamMapper;
import school.hei.stdgrade.security.model.Principal;
import school.hei.stdgrade.security.model.UserRole;
import school.hei.stdgrade.service.validator.CrupdateExamValidator;

@Service
@AllArgsConstructor
public class ExamService {
  private final JExamRepository jRepository;
  private final JExamMapper jMapper;
  private final CrupdateExamValidator crupdateValidator;
  private final JTeacherCourseAssignmentRepository jTeacherCourseAssignmentRepository;

  public List<Exam> getExamsByCourseAndYear(String courseId, String academicYearId) {
    return jRepository.findByCourseIdAndAcademicYearId(courseId, academicYearId).stream()
        .map(jMapper::toDomain)
        .toList();
  }

  public Exam getById(String id) {
    return jRepository
        .findById(id)
        .map(jMapper::toDomain)
        .orElseThrow(() -> new NoSuchElementException("Exam(id=" + id + ") not found"));
  }

  public Exam save(Exam toSave, Principal principal) {
    var isAdmin = principal.roles().contains(UserRole.ADMIN);
    var isAssignedTeacher =
        jTeacherCourseAssignmentRepository.existsByIdTeacherIdAndIdCourseIdAndIdAcademicYearId(
            principal.user().id(), toSave.courseId(), toSave.academicYearId());

    if (!isAdmin && !isAssignedTeacher) {
      throw new AccessDeniedException(
          "You are not authorized to create/modify exams for this course");
    }

    crupdateValidator.accept(toSave);

    var entity = jMapper.toEntity(toSave);
    return jMapper.toDomain(jRepository.save(entity));
  }
}
