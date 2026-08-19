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
@Table(name = "\"semester\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JSemester {
  @Id private String id;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(name = "\"position\"", nullable = false, unique = true)
  private int position;

  private int totalCredits;
}
