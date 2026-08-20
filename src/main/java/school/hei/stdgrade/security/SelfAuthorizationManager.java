package school.hei.stdgrade.security;

import static school.hei.stdgrade.security.model.UserRole.ADMIN;
import static school.hei.stdgrade.security.model.UserRole.TEACHER;

import java.util.function.Supplier;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import school.hei.stdgrade.security.model.Principal;

@Component
public class SelfAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

  @Override
  public AuthorizationDecision check(
      Supplier<Authentication> authentication, RequestAuthorizationContext context) {
    var auth = authentication.get();

    if (auth == null || !(auth.getPrincipal() instanceof Principal principal)) {
      return new AuthorizationDecision(false);
    }

    if (principal.roles().contains(ADMIN)) {
      return new AuthorizationDecision(true);
    }

    if (principal.roles().contains(TEACHER)) {
      return new AuthorizationDecision(true);
    }

    var variables = context.getVariables();
    var targetId = variables.get("userId");
    if (targetId == null) {
      targetId = variables.get("studentId");
    }
    if (targetId == null) {
      targetId = variables.get("teacherId");
    }

    var isSelf = targetId != null && targetId.equals(principal.user().id());

    return new AuthorizationDecision(isSelf);
  }
}
