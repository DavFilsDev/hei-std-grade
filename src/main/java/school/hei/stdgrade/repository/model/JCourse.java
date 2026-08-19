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
@Table(name = "\"course\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JCourse {
  @Id private String id;

  @Column(nullable = false, unique = true)
  private String ref;

  @Column(nullable = false)
  private String title;

  private int credits;

  @Column(name = "teaching_unit_id")
  private String teachingUnitId;

  @Column(name = "track_id")
  private String trackId; // nullable = common-core course, shared by both tracks
}
