package school.hei.stdgrade.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "\"student_group_assignment\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JStudentGroupAssignment {
  @EmbeddedId private JStudentGroupAssignmentId id;

  @Column(name = "group_id")
  private String groupId;
}
