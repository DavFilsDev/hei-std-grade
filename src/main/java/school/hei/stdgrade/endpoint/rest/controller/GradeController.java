package school.hei.stdgrade.endpoint.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.stdgrade.model.CorrectGradePayload;
import school.hei.stdgrade.model.Grade;
import school.hei.stdgrade.model.GradeHistoryEntry;
import school.hei.stdgrade.security.model.Principal;
import school.hei.stdgrade.service.GradeService;

@RestController
@AllArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class GradeController {
  private final GradeService service;

  @PutMapping("/grades")
  public Grade crupdateGrade(
      @RequestBody Grade toSave, @AuthenticationPrincipal Principal principal) {
    return service.save(toSave, principal);
  }

  @PatchMapping("/grades/{gradeId}")
  public Grade correctGrade(
      @PathVariable String gradeId,
      @RequestBody CorrectGradePayload payload,
      @AuthenticationPrincipal Principal principal) {
    return service.correctGrade(gradeId, payload.score(), payload.reason(), principal);
  }

  @GetMapping("/grades/{gradeId}/history")
  public List<GradeHistoryEntry> getGradeHistory(
      @PathVariable String gradeId, @AuthenticationPrincipal Principal principal) {
    return service.getGradeHistory(gradeId, principal);
  }

  @GetMapping("/students/{studentId}/grades")
  public List<Grade> getStudentGrades(
      @PathVariable String studentId, @AuthenticationPrincipal Principal principal) {
    return service.getStudentGrades(studentId, principal);
  }
}
