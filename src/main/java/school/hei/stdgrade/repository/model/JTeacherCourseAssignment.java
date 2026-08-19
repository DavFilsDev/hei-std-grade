package school.hei.stdgrade.repository.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "\"teacher_course_assignment\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JTeacherCourseAssignment {
  @EmbeddedId private JTeacherCourseAssignmentId id;
}
