package school.hei.stdgrade.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record UserWithToken(
    String id,
    String ref,
    String lastName,
    String firstName,
    String email,
    String passwordHash,
    boolean isEnabled,
    LocalDate entranceDate,
    String trackId,
    List<RoleName> roles,
    String token) {

  public static UserWithToken from(User user, String token) {
    return new UserWithToken(
        user.id(),
        user.ref(),
        user.lastName(),
        user.firstName(),
        user.email(),
        user.passwordHash(),
        user.isEnabled(),
        user.entranceDate(),
        user.trackId(),
        user.roles(),
        token);
  }
}
