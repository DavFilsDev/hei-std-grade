package school.hei.stdgrade.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Maps the "app_user" table (a bare "user" table name would collide with reserved SQL keywords).
@Entity
@Table(name = "\"app_user\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JUser {
  @Id private String id;

  @Column(nullable = false, unique = true)
  private String ref;

  private String lastName;
  private String firstName;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String passwordHash;

  @Column(nullable = false)
  private boolean isEnabled;

  private LocalDate entranceDate;

  // Nullable: null while the student is still in the common core (L1). Immutable once set.
  private String trackId;
}
