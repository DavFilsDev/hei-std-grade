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
public class JStudentGroupAssignmentId implements Serializable {
  @Column(name = "student_id")
  private String studentId;

  @Column(name = "academic_year_id")
  private String academicYearId;
}
