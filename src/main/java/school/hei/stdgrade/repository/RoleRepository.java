package school.hei.stdgrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.stdgrade.repository.model.JRole;

@Repository
public interface RoleRepository extends JpaRepository<JRole, String> {}
