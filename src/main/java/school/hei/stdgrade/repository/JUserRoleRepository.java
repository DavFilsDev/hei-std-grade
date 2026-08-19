package school.hei.stdgrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.stdgrade.repository.model.JUserRole;
import school.hei.stdgrade.repository.model.JUserRoleId;

public interface JUserRoleRepository extends JpaRepository<JUserRole, JUserRoleId> {
  // Task 1.1 (UserService) adds finder methods here, e.g.:
  // List<JUserRole> findByIdUserId(String userId);
  // void deleteByIdUserId(String userId);
}
