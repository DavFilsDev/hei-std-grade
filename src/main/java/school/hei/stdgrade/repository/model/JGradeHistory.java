package school.hei.stdgrade.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Append-only: rows are inserted by GradeService on every correction, never updated/deleted.
@Entity
@Table(name = "\"grade_history\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JGradeHistory {
  @Id private String id;

  @Column(name = "grade_id")
  private String gradeId;

  private double oldScore;
  private double newScore;

  @Column(nullable = false)
  private String reason;

  @Column(name = "updated_by")
  private String updatedBy;

  private Instant updatedAt;
}
