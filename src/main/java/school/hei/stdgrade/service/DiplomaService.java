package school.hei.stdgrade.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import school.hei.stdgrade.model.DiplomaStatus;
import school.hei.stdgrade.model.GraduateEntry;
import school.hei.stdgrade.model.User;
import school.hei.stdgrade.repository.JUserRepository;
import school.hei.stdgrade.repository.mapper.JUserMapper;
import school.hei.stdgrade.security.model.Principal;
import school.hei.stdgrade.security.model.UserRole;

@Service
@AllArgsConstructor
public class DiplomaService {
  private final JUserRepository jUserRepository;
  private final JUserMapper jUserMapper;
  private final GradeCalculationService gradeCalculationService;

  public List<Integer> getAllPromotions() {
    return jUserRepository.findAll().stream()
        .map(u -> u.getEntranceDate())
        .filter(Objects::nonNull)
        .map(LocalDate::getYear)
        .distinct()
        .sorted()
        .toList();
  }

  public List<GraduateEntry> getGraduatesByPromotion(int promotionYear) {
    record Scored(User user, double average) {}

    var scored =
        jUserRepository.findAll().stream()
            .map(jUserMapper::toDomain)
            .filter(u -> u.entranceDate() != null && u.entranceDate().getYear() == promotionYear)
            .filter(u -> gradeCalculationService.isDiplomed(u.id()))
            .map(u -> new Scored(u, gradeCalculationService.computeThreeYearAverage(u.id())))
            .sorted(Comparator.comparingDouble(Scored::average).reversed())
            .toList();

    var result = new ArrayList<GraduateEntry>();
    for (int i = 0; i < scored.size(); i++) {
      var s = scored.get(i);
      result.add(
          new GraduateEntry(
              i + 1, s.user().ref(), s.user().lastName(), s.user().firstName(), s.average()));
    }
    return result;
  }

  public DiplomaStatus getStudentDiplomaStatus(String studentId, Principal principal) {
    var isAdmin = principal.roles().contains(UserRole.ADMIN);
    var isSelf = principal.user().id().equals(studentId);
    if (!isAdmin && !isSelf) {
      throw new AccessDeniedException("You are not authorized to view this diploma status");
    }
    return new DiplomaStatus(gradeCalculationService.isDiplomed(studentId));
  }
}
