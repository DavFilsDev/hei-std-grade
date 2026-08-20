package school.hei.stdgrade.service.validator;

import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.AcademicTrack;
import school.hei.stdgrade.repository.JAcademicTrackRepository;

@Component
@AllArgsConstructor
public class CrupdateAcademicTrackValidator implements Consumer<AcademicTrack> {

  private final JAcademicTrackRepository jAcademicTrackRepository;

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  @Override
  public void accept(AcademicTrack track) {
    var sj = new StringJoiner(". ");

    if (isBlank(track.id())) {
      sj.add("Id is mandatory");
    }
    if (isBlank(track.code())) {
      sj.add("Code is mandatory");
    }
    if (isBlank(track.name())) {
      sj.add("Name is mandatory");
    }

    if (!isBlank(track.code())) {
      var validCodes = Set.of("TN", "EL");
      if (!validCodes.contains(track.code())) {
        sj.add("Code must be either 'TN' or 'EL'");
      }
    }

    if (!isBlank(track.id())
        && !isBlank(track.code())
        && jAcademicTrackRepository.existsByCodeAndIdNot(track.code(), track.id())) {
      sj.add("Code already taken");
    }

    if (sj.length() > 0) {
      throw new IllegalArgumentException(sj.toString());
    }
  }
}
