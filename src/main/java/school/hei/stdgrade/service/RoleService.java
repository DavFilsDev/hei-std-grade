package school.hei.stdgrade.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.stdgrade.model.Role;
import school.hei.stdgrade.repository.RoleRepository;
import school.hei.stdgrade.repository.mapper.JRoleMapper;

@Service
@AllArgsConstructor
public class RoleService {
  private final RoleRepository repository;
  private final JRoleMapper mapper;

  public List<Role> findAll() {
    return repository.findAll().stream().map(mapper::toDomain).toList();
  }
}
