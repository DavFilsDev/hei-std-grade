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

@Entity
@Table(name = "\"exam\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JExam {
  @Id private String id;

  @Column(name = "exam_date", nullable = false)
  private Instant examDate;

  // Nullable: mandatory for REGULAR, always null for RETAKE.
  private Double coefficient;

  @Column(name = "session_type", nullable = false)
  private String sessionType; // "REGULAR" | "RETAKE"

  @Column(name = "course_id")
  private String courseId;

  @Column(name = "academic_year_id")
  private String academicYearId;
}
