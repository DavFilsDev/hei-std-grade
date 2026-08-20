package school.hei.stdgrade.endpoint.rest.controller;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.stdgrade.model.TranscriptSummary;
import school.hei.stdgrade.security.model.Principal;
import school.hei.stdgrade.service.TranscriptService;

@RestController
@AllArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class TranscriptController {
  private final TranscriptService service;

  @GetMapping("/students/{studentId}/academic-years/{academicYearId}/transcript-summary")
  public TranscriptSummary getTranscriptSummary(
      @PathVariable String studentId,
      @PathVariable String academicYearId,
      @AuthenticationPrincipal Principal principal) {
    return service.getTranscriptSummary(studentId, academicYearId, principal);
  }

  @PostMapping("/students/{studentId}/academic-years/{academicYearId}/transcript-requests")
  public ResponseEntity<Void> requestTranscriptGeneration(
      @PathVariable String studentId,
      @PathVariable String academicYearId,
      @AuthenticationPrincipal Principal principal) {
    service.requestTranscriptGeneration(studentId, academicYearId, principal);
    return ResponseEntity.status(ACCEPTED).build();
  }
}
