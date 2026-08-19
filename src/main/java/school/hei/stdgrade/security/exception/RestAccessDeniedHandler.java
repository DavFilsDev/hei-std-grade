package school.hei.stdgrade.security.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void handle(
      HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
      throws IOException {
    response.setStatus(FORBIDDEN.value());
    response.setContentType("application/json");
    response
        .getWriter()
        .write(
            mapper.writeValueAsString(
                Map.of(
                    "message", e.getMessage(),
                    "type", FORBIDDEN.toString())));
  }
}
