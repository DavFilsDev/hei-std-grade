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
@Table(name = "\"class_group\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JClassGroup {
  @Id private String id;

  @Column(nullable = false, unique = true)
  private String ref;
}
