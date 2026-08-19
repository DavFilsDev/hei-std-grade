package school.hei.stdgrade.security.model;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import school.hei.stdgrade.model.User;

/**
 * Unlike cine-app (one role per user), this project has a many-to-many user_role table (ex: a
 * teacher who is also an admin) — so authorities come from a resolved {@code roles} list rather
 * than a single field on {@code user}. See UserService.loadUserByUsername (Task 1.1), which must
 * load both the user and their roles before building this record.
 */
public record Principal(User user, List<UserRole> roles) implements UserDetails {

  @Override
  public List<UserRole> getAuthorities() {
    return roles;
  }

  @Override
  public String getPassword() {
    return user.passwordHash();
  }

  @Override
  public String getUsername() {
    return user.email();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return user.isEnabled();
  }
}
