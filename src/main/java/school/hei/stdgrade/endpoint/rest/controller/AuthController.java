package school.hei.stdgrade.endpoint.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.stdgrade.model.LoginPayload;
import school.hei.stdgrade.model.UserWithToken;
import school.hei.stdgrade.service.UserService;

@RestController
@AllArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class AuthController {
  private final UserService service;

  @PostMapping("/login")
  public UserWithToken login(@RequestBody LoginPayload payload) {
    return service.login(payload.email(), payload.password());
  }
}
