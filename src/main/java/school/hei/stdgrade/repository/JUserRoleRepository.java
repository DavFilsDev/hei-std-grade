package school.hei.stdgrade.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.stdgrade.repository.model.JUserRole;
import school.hei.stdgrade.repository.model.JUserRoleId;

public interface JUserRoleRepository extends JpaRepository<JUserRole, JUserRoleId> {
  List<JUserRole> findByIdUserId(String userId);

  void deleteByIdUserId(String userId);
}
