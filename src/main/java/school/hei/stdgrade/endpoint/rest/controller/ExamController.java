package school.hei.stdgrade.endpoint.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.stdgrade.model.Exam;
import school.hei.stdgrade.security.model.Principal;
import school.hei.stdgrade.service.ExamService;

@RestController
@AllArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class ExamController {
  private final ExamService service;

  @GetMapping("/courses/{courseId}/academic-years/{academicYearId}/exams")
  public List<Exam> getExamsByCourseAndYear(
      @PathVariable String courseId, @PathVariable String academicYearId) {
    return service.getExamsByCourseAndYear(courseId, academicYearId);
  }

  @GetMapping("/exams/{examId}")
  public Exam getExamById(@PathVariable String examId) {
    return service.getById(examId);
  }

  @PutMapping("/courses/{courseId}/academic-years/{academicYearId}/exams")
  public Exam crupdateExam(
      @PathVariable String courseId,
      @PathVariable String academicYearId,
      @RequestBody Exam toSave,
      @AuthenticationPrincipal Principal principal) {
    return service.save(
        toSave.toBuilder().courseId(courseId).academicYearId(academicYearId).build(), principal);
  }
}
