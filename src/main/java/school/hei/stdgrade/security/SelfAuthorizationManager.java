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

// Adapted from cine-app: isStaff now covers TEACHER or ADMIN instead of EMPLOYEE/MANAGER.
@Component
public class SelfAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

  @Override
  public AuthorizationDecision check(
      Supplier<Authentication> authentication, RequestAuthorizationContext context) {
    var auth = authentication.get();

    if (auth == null || !(auth.getPrincipal() instanceof Principal principal)) {
      return new AuthorizationDecision(false);
    }

    var uid = context.getVariables().get("userId");
    var isStaff = principal.roles().contains(TEACHER) || principal.roles().contains(ADMIN);
    var isSelf = uid != null && uid.equals(principal.user().id());

    return new AuthorizationDecision(isStaff || isSelf);
  }
}
