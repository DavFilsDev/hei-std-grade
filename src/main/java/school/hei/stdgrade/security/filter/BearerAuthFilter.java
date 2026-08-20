package school.hei.stdgrade.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import school.hei.stdgrade.security.jwt.JwtService;

@Component
@AllArgsConstructor
public class BearerAuthFilter extends OncePerRequestFilter {
  private static final String HEADER = "Authorization";
  private static final String PREFIX = "Bearer ";

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    var header = request.getHeader(HEADER);

    if (header != null && header.startsWith(PREFIX)) {
      var token = header.substring(PREFIX.length());
      var email = safeExtractUsername(token);

      if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        try {
          var principal = userDetailsService.loadUserByUsername(email);

          if (jwtService.isValid(token, email)) {
            var authentication =
                new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        } catch (Exception ignored) {
          // leave the SecurityContext unauthenticated; the entry point will produce the 401
        }
      }
    }

    chain.doFilter(request, response);
  }

  private String safeExtractUsername(String token) {
    try {
      return jwtService.extractUsername(token);
    } catch (Exception e) {
      return null;
    }
  }
}
