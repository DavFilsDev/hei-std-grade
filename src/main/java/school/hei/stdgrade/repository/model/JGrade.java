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
@Table(name = "\"grade\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JGrade {
  @Id private String id;

  @Column(name = "student_id")
  private String studentId;

  @Column(name = "exam_id")
  private String examId;

  private double score;
}
