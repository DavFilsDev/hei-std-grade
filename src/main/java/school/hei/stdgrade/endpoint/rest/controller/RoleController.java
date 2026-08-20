package school.hei.stdgrade.endpoint.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.stdgrade.model.Role;
import school.hei.stdgrade.service.RoleService;

@RestController
@AllArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class RoleController {
  private final RoleService service;

  @GetMapping("/roles")
  public List<Role> getRoles() {
    return service.findAll();
  }
}
