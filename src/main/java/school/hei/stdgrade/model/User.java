package school.hei.stdgrade.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

/**
 * A user of the platform (student, teacher, or admin). A user can hold several roles at once (ex: a
 * teacher who is also an admin), hence the {@code roles} list instead of a single field.
 *
 * <p>{@code trackId} is nullable: a student still in the common core (L1) has none yet. Once set by
 * an ADMIN, it is immutable — see UserService.assignTrack (Task 1.1).
 */
@Builder(toBuilder = true)
public record User(
    String id,
    String ref,
    String lastName,
    String firstName,
    String email,
    String passwordHash,
    boolean isEnabled,
    LocalDate entranceDate,
    String trackId,
    List<RoleName> roles) {}
