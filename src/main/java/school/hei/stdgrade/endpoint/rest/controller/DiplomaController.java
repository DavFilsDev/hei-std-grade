package school.hei.stdgrade.endpoint.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.stdgrade.file.diploma.GraduatesXlsxGenerator;
import school.hei.stdgrade.model.DiplomaStatus;
import school.hei.stdgrade.model.GraduateEntry;
import school.hei.stdgrade.security.model.Principal;
import school.hei.stdgrade.service.DiplomaService;

@RestController
@AllArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class DiplomaController {
  private final DiplomaService service;
  private final GraduatesXlsxGenerator xlsxGenerator;

  @GetMapping("/promotions")
  public List<Integer> getAllPromotions() {
    return service.getAllPromotions();
  }

  @GetMapping("/promotions/{promotionYear}/graduates")
  public List<GraduateEntry> getGraduatesByPromotion(@PathVariable int promotionYear) {
    return service.getGraduatesByPromotion(promotionYear);
  }

  @GetMapping("/promotions/{promotionYear}/graduates.xlsx")
  public ResponseEntity<byte[]> downloadGraduatesXlsx(@PathVariable int promotionYear) {
    var graduates = service.getGraduatesByPromotion(promotionYear);
    var content = xlsxGenerator.generate(graduates);
    var filename = "diplomes-promotion-" + promotionYear + ".xlsx";

    return ResponseEntity.ok()
        .contentType(GraduatesXlsxGenerator.XLSX_MEDIA_TYPE)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
        .body(content);
  }

  @GetMapping("/students/{studentId}/diploma-status")
  public DiplomaStatus getStudentDiplomaStatus(
      @PathVariable String studentId, @AuthenticationPrincipal Principal principal) {
    return service.getStudentDiplomaStatus(studentId, principal);
  }
}
