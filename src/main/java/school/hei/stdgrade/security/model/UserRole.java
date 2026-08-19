package school.hei.stdgrade.security.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
  STUDENT,
  TEACHER,
  ADMIN;

  public String role() {
    return name();
  }

  @Override
  public String getAuthority() {
    return "ROLE_" + role();
  }

  @Override
  public String toString() {
    return role();
  }
}
