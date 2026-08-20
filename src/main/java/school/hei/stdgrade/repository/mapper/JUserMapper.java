package school.hei.stdgrade.repository.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.RoleName;
import school.hei.stdgrade.model.User;
import school.hei.stdgrade.repository.model.JUser;

@Component
public class JUserMapper {
  public User toDomain(JUser entity) {
    return toDomain(entity, List.of());
  }

  public User toDomain(JUser entity, List<RoleName> roles) {
    return new User(
        entity.getId(),
        entity.getRef(),
        entity.getLastName(),
        entity.getFirstName(),
        entity.getEmail(),
        entity.getPasswordHash(),
        entity.isEnabled(),
        entity.getEntranceDate(),
        entity.getTrackId(),
        roles);
  }

  public JUser toEntity(User domain) {
    JUser entity = new JUser();
    entity.setId(domain.id());
    entity.setRef(domain.ref());
    entity.setLastName(domain.lastName());
    entity.setFirstName(domain.firstName());
    entity.setEmail(domain.email());
    entity.setPasswordHash(domain.passwordHash());
    entity.setEnabled(domain.isEnabled());
    entity.setEntranceDate(domain.entranceDate());
    entity.setTrackId(domain.trackId());
    return entity;
  }
}
