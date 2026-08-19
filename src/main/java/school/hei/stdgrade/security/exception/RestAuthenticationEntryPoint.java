package school.hei.stdgrade.security.exception;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
      throws IOException {
    response.setStatus(UNAUTHORIZED.value());
    response.setContentType("application/json");
    response
        .getWriter()
        .write(
            mapper.writeValueAsString(
                Map.of(
                    "message", e.getMessage(),
                    "type", UNAUTHORIZED.toString())));
  }
}
