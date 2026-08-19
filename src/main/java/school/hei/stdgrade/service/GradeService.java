package school.hei.stdgrade.service;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.hei.stdgrade.model.Grade;
import school.hei.stdgrade.model.GradeHistoryEntry;
import school.hei.stdgrade.repository.JExamRepository;
import school.hei.stdgrade.repository.JGradeHistoryRepository;
import school.hei.stdgrade.repository.JGradeRepository;
import school.hei.stdgrade.repository.JTeacherCourseAssignmentRepository;
import school.hei.stdgrade.repository.mapper.JGradeHistoryMapper;
import school.hei.stdgrade.repository.mapper.JGradeMapper;
import school.hei.stdgrade.security.model.Principal;
import school.hei.stdgrade.security.model.UserRole;
import school.hei.stdgrade.service.validator.CrupdateGradeValidator;

@Service
@AllArgsConstructor
public class GradeService {
  private final JGradeRepository jRepository;
  private final JGradeMapper jMapper;
  private final JGradeHistoryRepository jHistoryRepository;
  private final JGradeHistoryMapper jHistoryMapper;
  private final CrupdateGradeValidator crupdateValidator;
  private final JExamRepository jExamRepository;
  private final JTeacherCourseAssignmentRepository jTeacherCourseAssignmentRepository;

  public List<Grade> getStudentGrades(String studentId, Principal requester) {
    var isAdmin = requester.roles().contains(UserRole.ADMIN);
    var isTeacher = requester.roles().contains(UserRole.TEACHER);
    var isSelf = requester.user().id().equals(studentId);

    if (!isAdmin && !isTeacher && !isSelf) {
      throw new AccessDeniedException("You are not authorized to view these grades");
    }

    return jRepository.findByStudentId(studentId).stream().map(jMapper::toDomain).toList();
  }

  @Transactional
  public Grade save(Grade toSave, Principal principal) {
    var isAdmin = principal.roles().contains(UserRole.ADMIN);
    var exam =
        jExamRepository
            .findById(toSave.examId())
            .orElseThrow(
                () -> new NoSuchElementException("Exam(id=" + toSave.examId() + ") not found"));
    var isAssignedTeacher =
        jTeacherCourseAssignmentRepository.existsByIdTeacherIdAndIdCourseIdAndIdAcademicYearId(
            principal.user().id(), exam.getCourseId(), exam.getAcademicYearId());

    if (!isAdmin && !isAssignedTeacher) {
      throw new AccessDeniedException("You are not authorized to create grades for this exam");
    }

    crupdateValidator.accept(toSave);

    var entity = jMapper.toEntity(toSave);
    return jMapper.toDomain(jRepository.save(entity));
  }

  @Transactional
  public Grade correctGrade(String gradeId, double newScore, String reason, Principal principal) {
    if (reason == null || reason.isBlank()) {
      throw new IllegalArgumentException("Reason is mandatory for grade correction");
    }

    var existing =
        jRepository
            .findById(gradeId)
            .orElseThrow(() -> new NoSuchElementException("Grade(id=" + gradeId + ") not found"));

    var isAdmin = principal.roles().contains(UserRole.ADMIN);
    var exam =
        jExamRepository
            .findById(existing.getExamId())
            .orElseThrow(
                () ->
                    new NoSuchElementException("Exam(id=" + existing.getExamId() + ") not found"));
    var isAssignedTeacher =
        jTeacherCourseAssignmentRepository.existsByIdTeacherIdAndIdCourseIdAndIdAcademicYearId(
            principal.user().id(), exam.getCourseId(), exam.getAcademicYearId());

    if (!isAdmin && !isAssignedTeacher) {
      throw new AccessDeniedException("You are not authorized to correct grades for this exam");
    }

    if (newScore < 0 || newScore > 20) {
      throw new IllegalArgumentException("Score must be between 0 and 20");
    }

    double oldScore = existing.getScore();

    GradeHistoryEntry historyEntry =
        new GradeHistoryEntry(
            randomUUID().toString(),
            gradeId,
            oldScore,
            newScore,
            reason,
            principal.user().id(),
            now());
    jHistoryRepository.save(jHistoryMapper.toEntity(historyEntry));

    existing.setScore(newScore);
    var updated = jRepository.save(existing);

    return jMapper.toDomain(updated);
  }

  public List<GradeHistoryEntry> getGradeHistory(String gradeId, Principal principal) {
    var grade =
        jRepository
            .findById(gradeId)
            .orElseThrow(() -> new NoSuchElementException("Grade(id=" + gradeId + ") not found"));

    var isAdmin = principal.roles().contains(UserRole.ADMIN);
    var exam =
        jExamRepository
            .findById(grade.getExamId())
            .orElseThrow(
                () -> new NoSuchElementException("Exam(id=" + grade.getExamId() + ") not found"));
    var isAssignedTeacher =
        jTeacherCourseAssignmentRepository.existsByIdTeacherIdAndIdCourseIdAndIdAcademicYearId(
            principal.user().id(), exam.getCourseId(), exam.getAcademicYearId());

    if (!isAdmin && !isAssignedTeacher) {
      throw new AccessDeniedException("You are not authorized to view history for this grade");
    }

    return jHistoryRepository.findByGradeId(gradeId).stream()
        .map(jHistoryMapper::toDomain)
        .toList();
  }
}
