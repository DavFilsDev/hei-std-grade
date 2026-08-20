package school.hei.stdgrade.service.validator;

import java.util.StringJoiner;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.CrupdateUserPayload;
import school.hei.stdgrade.repository.JUserRepository;

@Component
@AllArgsConstructor
public class CrupdateUserValidator implements Consumer<CrupdateUserPayload> {
  private final JUserRepository jUserRepository;

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  @Override
  public void accept(CrupdateUserPayload payload) {
    var sj = new StringJoiner(". ");

    if (isBlank(payload.ref())) {
      sj.add("Ref is mandatory");
    } else if (jUserRepository.existsByRefAndIdNot(payload.ref(), payload.id())) {
      sj.add("Ref(" + payload.ref() + ") is already taken");
    }
    if (isBlank(payload.lastName())) {
      sj.add("LastName is mandatory");
    }
    if (isBlank(payload.firstName())) {
      sj.add("FirstName is mandatory");
    }
    if (isBlank(payload.email())) {
      sj.add("Email is mandatory");
    } else if (jUserRepository.existsByEmailAndIdNot(payload.email(), payload.id())) {
      sj.add("Email(" + payload.email() + ") is already taken");
    }
    if (payload.roles() == null || payload.roles().isEmpty()) {
      sj.add("At least one role is mandatory");
    }

    var isCreation = !jUserRepository.existsById(payload.id());
    if (isCreation && isBlank(payload.password())) {
      sj.add("Password is mandatory at creation");
    }

    if (sj.length() > 0) {
      throw new IllegalArgumentException(sj.toString());
    }
  }
}
