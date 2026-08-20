package school.hei.stdgrade.endpoint.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class WebExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ModelAndView handleException(Exception e) {
    log.error("Exception in web controller: ", e);
    ModelAndView mav = new ModelAndView("error");
    mav.addObject("message", "Une erreur est survenue : " + e.getMessage());
    return mav;
  }
}
