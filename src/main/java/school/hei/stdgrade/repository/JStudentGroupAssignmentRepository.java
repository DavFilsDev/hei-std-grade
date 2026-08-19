package school.hei.stdgrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.stdgrade.repository.model.JStudentGroupAssignment;
import school.hei.stdgrade.repository.model.JStudentGroupAssignmentId;

public interface JStudentGroupAssignmentRepository
    extends JpaRepository<JStudentGroupAssignment, JStudentGroupAssignmentId> {}
