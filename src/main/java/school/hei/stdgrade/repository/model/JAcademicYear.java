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
@Table(name = "\"academic_year\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JAcademicYear {
  @Id private String id;

  @Column(nullable = false, unique = true)
  private String label;

  // Used to resolve the "most recent attempt" when a student repeats a year (see grading rules).
  @Column(name = "start_year", nullable = false, unique = true)
  private int startYear;
}
