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
public class JTeacherCourseAssignmentId implements Serializable {
  @Column(name = "teacher_id")
  private String teacherId;

  @Column(name = "course_id")
  private String courseId;

  @Column(name = "academic_year_id")
  private String academicYearId;
}
