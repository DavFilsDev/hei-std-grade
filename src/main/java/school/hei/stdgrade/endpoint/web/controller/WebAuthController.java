package school.hei.stdgrade.endpoint.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebAuthController {

  @GetMapping("/web/login")
  public String loginPage() {
    return "web/login";
  }
}
