package school.hei.stdgrade.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "\"role\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JRole {
  @Id private String id;

  @Column(nullable = false, unique = true)
  private String name; // RoleName as string ("ROLE_STUDENT", "ROLE_TEACHER", "ROLE_ADMIN")
}
