package school.hei.stdgrade.endpoint.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.hei.stdgrade.model.AssignTrackPayload;
import school.hei.stdgrade.model.CrupdateUserPayload;
import school.hei.stdgrade.model.RoleName;
import school.hei.stdgrade.model.User;
import school.hei.stdgrade.service.UserService;

@RestController
@AllArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class UserController {
  private final UserService service;

  @GetMapping("/users")
  public List<User> getAllUsers(@RequestParam(required = false) RoleName role) {
    return service.findAll(role);
  }

  @GetMapping("/users/{userId}")
  public User getUserById(@PathVariable String userId) {
    return service.getById(userId);
  }

  @PutMapping("/users")
  public User crupdateUser(@RequestBody CrupdateUserPayload payload) {
    return service.crupdate(payload);
  }

  @PutMapping("/users/{userId}/track")
  public User assignStudentTrack(
      @PathVariable String userId, @RequestBody AssignTrackPayload payload) {
    return service.assignTrack(userId, payload.trackId());
  }
}
