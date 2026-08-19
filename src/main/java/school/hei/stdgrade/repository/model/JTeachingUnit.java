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
@Table(name = "\"teaching_unit\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JTeachingUnit {
  @Id private String id;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = false)
  private String title;

  @Column(name = "semester_id")
  private String semesterId;

  private int credits;
}
