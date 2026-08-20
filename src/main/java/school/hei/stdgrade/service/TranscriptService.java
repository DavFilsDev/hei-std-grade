package school.hei.stdgrade.service;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import school.hei.stdgrade.endpoint.event.EventProducer;
import school.hei.stdgrade.endpoint.event.model.TranscriptRequestedEvent;
import school.hei.stdgrade.model.TranscriptSummary;
import school.hei.stdgrade.repository.JUserRepository;
import school.hei.stdgrade.security.model.Principal;
import school.hei.stdgrade.security.model.UserRole;

@Slf4j
@Service
@AllArgsConstructor
public class TranscriptService {
  private final GradeCalculationService gradeCalculationService;
  private final JUserRepository jUserRepository;
  private final EventProducer<TranscriptRequestedEvent> eventProducer;

  public TranscriptSummary getTranscriptSummary(
      String studentId, String academicYearId, Principal principal) {
    checkSelfOrAdmin(studentId, principal);
    checkStudentExists(studentId);
    return gradeCalculationService.computeTranscript(studentId, academicYearId);
  }

  public void requestTranscriptGeneration(
      String studentId, String academicYearId, Principal principal) {
    checkSelfOrAdmin(studentId, principal);
    checkStudentExists(studentId);

    try {
      eventProducer.accept(List.of(new TranscriptRequestedEvent(studentId, academicYearId)));
    } catch (RuntimeException e) {
      log.warn(
          "Failed to publish TranscriptRequestedEvent(studentId={}, academicYearId={}): {}",
          studentId,
          academicYearId,
          e.getMessage());
    }
  }

  private void checkStudentExists(String studentId) {
    if (!jUserRepository.existsById(studentId)) {
      throw new NoSuchElementException("User(id=" + studentId + ") not found");
    }
  }

  private void checkSelfOrAdmin(String studentId, Principal principal) {
    var isAdmin = principal.roles().contains(UserRole.ADMIN);
    var isSelf = principal.user().id().equals(studentId);
    if (!isAdmin && !isSelf) {
      throw new AccessDeniedException("You are not authorized to access this transcript");
    }
  }
}
