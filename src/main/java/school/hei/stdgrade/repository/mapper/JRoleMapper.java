package school.hei.stdgrade.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.Role;
import school.hei.stdgrade.model.RoleName;
import school.hei.stdgrade.repository.model.JRole;

@Component
public class JRoleMapper {
  public Role toDomain(JRole entity) {
    return Role.builder().id(entity.getId()).name(RoleName.valueOf(entity.getName())).build();
  }

  public JRole toEntity(Role domain) {
    JRole entity = new JRole();
    entity.setId(domain.id());
    entity.setName(domain.name().name());
    return entity;
  }
}
