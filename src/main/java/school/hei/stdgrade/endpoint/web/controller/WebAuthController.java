package school.hei.stdgrade.endpoint.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class WebAuthController {

  @GetMapping("/web/login")
  public String loginPage() {
    log.info("=== Login page requested, returning view 'web/login' ===");
    return "web/login";
  }
}
