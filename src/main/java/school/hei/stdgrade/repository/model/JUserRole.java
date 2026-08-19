package school.hei.stdgrade.repository.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "\"user_role\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JUserRole {
  @EmbeddedId private JUserRoleId id;
}
