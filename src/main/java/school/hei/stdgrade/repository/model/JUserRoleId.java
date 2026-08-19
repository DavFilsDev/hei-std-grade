package school.hei.stdgrade.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class JUserRoleId implements Serializable {
  @Column(name = "user_id")
  private String userId;

  @Column(name = "role_id")
  private String roleId;
}
