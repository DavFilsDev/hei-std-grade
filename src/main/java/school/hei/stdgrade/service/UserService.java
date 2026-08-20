package school.hei.stdgrade.service;

import static java.util.UUID.randomUUID;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.hei.stdgrade.model.CrupdateUserPayload;
import school.hei.stdgrade.model.RoleName;
import school.hei.stdgrade.model.User;
import school.hei.stdgrade.model.UserWithToken;
import school.hei.stdgrade.repository.JAcademicTrackRepository;
import school.hei.stdgrade.repository.JRoleRepository;
import school.hei.stdgrade.repository.JUserRepository;
import school.hei.stdgrade.repository.JUserRoleRepository;
import school.hei.stdgrade.repository.mapper.JUserMapper;
import school.hei.stdgrade.repository.model.JUser;
import school.hei.stdgrade.repository.model.JUserRole;
import school.hei.stdgrade.repository.model.JUserRoleId;
import school.hei.stdgrade.security.jwt.JwtService;
import school.hei.stdgrade.security.model.Principal;
import school.hei.stdgrade.security.model.UserRole;
import school.hei.stdgrade.service.validator.CrupdateUserValidator;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
  private final JUserRepository jUserRepository;
  private final JUserRoleRepository jUserRoleRepository;
  private final JRoleRepository jRoleRepository;
  private final JAcademicTrackRepository jAcademicTrackRepository;
  private final JUserMapper jUserMapper;
  private final CrupdateUserValidator crupdateValidator;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public List<User> findAll(RoleName roleFilter) {
    var users = jUserRepository.findAll().stream().map(this::toDomainWithRoles).toList();

    if (roleFilter == null) {
      return users;
    }
    return users.stream().filter(user -> user.roles().contains(roleFilter)).toList();
  }

  public User getById(String id) {
    var entity =
        jUserRepository
            .findById(id)
            .orElseThrow(() -> new NoSuchElementException("User(id=" + id + ") not found"));
    return toDomainWithRoles(entity);
  }

  @Transactional
  public User crupdate(CrupdateUserPayload rawPayload) {

    var payload =
        isBlank(rawPayload.id())
            ? rawPayload.toBuilder().id(randomUUID().toString()).build()
            : rawPayload;

    crupdateValidator.accept(payload);

    var existing = jUserRepository.findById(payload.id());

    var entity = existing.orElseGet(JUser::new);
    entity.setId(payload.id());
    entity.setRef(payload.ref());
    entity.setLastName(payload.lastName());
    entity.setFirstName(payload.firstName());
    entity.setEmail(payload.email());
    entity.setEnabled(payload.isEnabled());
    entity.setEntranceDate(payload.entranceDate());
    entity.setTrackId(payload.trackId());

    if (existing.isEmpty()) {
      entity.setPasswordHash(passwordEncoder.encode(payload.password()));
    } else if (!isBlank(payload.password())) {
      entity.setPasswordHash(passwordEncoder.encode(payload.password()));
    }

    var saved = jUserRepository.save(entity);

    jUserRoleRepository.deleteByIdUserId(saved.getId());
    var roleEntities = jRoleRepository.findAll();
    for (RoleName roleName : payload.roles()) {
      var jRole =
          roleEntities.stream()
              .filter(r -> r.getName().equals(roleName.name()))
              .findFirst()
              .orElseThrow(
                  () -> new NoSuchElementException("Role(name=" + roleName + ") not found"));
      jUserRoleRepository.save(new JUserRole(new JUserRoleId(saved.getId(), jRole.getId())));
    }

    return toDomainWithRoles(saved);
  }

  @Transactional
  public User assignTrack(String userId, String trackId) {
    var entity =
        jUserRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User(id=" + userId + ") not found"));

    if (entity.getTrackId() != null) {
      throw new IllegalArgumentException(
          "User(id=" + userId + ") already has a track assigned, it is immutable once set");
    }
    if (isBlank(trackId)) {
      throw new IllegalArgumentException("TrackId is mandatory");
    }
    if (!jAcademicTrackRepository.existsById(trackId)) {
      throw new IllegalArgumentException("AcademicTrack(id=" + trackId + ") does not exist");
    }

    entity.setTrackId(trackId);
    var saved = jUserRepository.save(entity);
    return toDomainWithRoles(saved);
  }

  public UserWithToken login(String email, String password) {
    var principal = loadUserByUsername(email);

    if (!passwordEncoder.matches(password, principal.getPassword())) {
      throw new BadCredentialsException("Invalid email or password");
    }

    var token = jwtService.generate(principal);
    return UserWithToken.from(principal.user(), token);
  }

  @Override
  public Principal loadUserByUsername(String email) throws UsernameNotFoundException {
    var entity =
        jUserRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

    var user = toDomainWithRoles(entity);
    var roles = user.roles().stream().map(this::toSecurityRole).toList();
    return new Principal(user, roles);
  }

  private User toDomainWithRoles(JUser entity) {
    var roleNames =
        jUserRoleRepository.findByIdUserId(entity.getId()).stream()
            .map(
                jUserRole ->
                    jRoleRepository
                        .findById(jUserRole.getId().getRoleId())
                        .orElseThrow(
                            () ->
                                new NoSuchElementException(
                                    "Role(id=" + jUserRole.getId().getRoleId() + ") not found")))
            .map(jRole -> RoleName.valueOf(jRole.getName()))
            .toList();

    return jUserMapper.toDomain(entity, roleNames);
  }

  private UserRole toSecurityRole(RoleName roleName) {
    return switch (roleName) {
      case ROLE_STUDENT -> UserRole.STUDENT;
      case ROLE_TEACHER -> UserRole.TEACHER;
      case ROLE_ADMIN -> UserRole.ADMIN;
    };
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
