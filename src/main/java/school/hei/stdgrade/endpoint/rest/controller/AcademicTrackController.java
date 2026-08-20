package school.hei.stdgrade.endpoint.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.stdgrade.model.AcademicTrack;
import school.hei.stdgrade.service.AcademicTrackService;

@RestController
@AllArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class AcademicTrackController {

  private final AcademicTrackService service;

  @GetMapping("/academic-tracks")
  public List<AcademicTrack> getAllAcademicTracks() {
    return service.findAll();
  }

  @PutMapping("/academic-tracks")
  public AcademicTrack crupdateAcademicTrack(@RequestBody AcademicTrack toSave) {
    return service.save(toSave);
  }
}
