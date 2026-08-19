package school.hei.stdgrade.security.jwt;

import static java.time.Instant.now;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.hei.stdgrade.security.model.Principal;

@Component
public class JwtService {
  private final SecretKey key;
  private final long expirationMs;

  public JwtService(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
    this.expirationMs = expirationMs;
  }

  public String generate(Principal principal) {
    var now = now();
    return Jwts.builder()
        .subject(principal.getUsername())
        .claim("uid", principal.user().id())
        // List of role names, unlike cine-app's single "role" claim (see Principal).
        .claim("roles", principal.roles().stream().map(Enum::name).toList())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusMillis(expirationMs)))
        .signWith(key)
        .compact();
  }

  public String extractUsername(String token) {
    return parse(token).getPayload().getSubject();
  }

  @SuppressWarnings("unchecked")
  public List<String> extractRoles(String token) {
    return (List<String>) parse(token).getPayload().get("roles", List.class);
  }

  public boolean isValid(String token, String username) {
    try {
      var claims = parse(token).getPayload();
      return claims.getSubject().equals(username) && claims.getExpiration().after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  private Jws<Claims> parse(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
  }
}
