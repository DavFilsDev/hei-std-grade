package school.hei.stdgrade.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record CrupdateUserPayload(
    String id,
    String ref,
    String lastName,
    String firstName,
    String email,
    String password,
    boolean isEnabled,
    LocalDate entranceDate,
    String trackId,
    List<RoleName> roles) {}
