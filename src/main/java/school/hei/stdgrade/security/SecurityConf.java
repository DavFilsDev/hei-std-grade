package school.hei.stdgrade.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import school.hei.stdgrade.security.exception.RestAccessDeniedHandler;
import school.hei.stdgrade.security.exception.RestAuthenticationEntryPoint;
import school.hei.stdgrade.security.filter.BearerAuthFilter;

/**
 * Skeleton only (Task 0) — the two matcher methods below are filled independently by Dev 1
 * (identity & academic structure) and Dev 2 (grades & transcript), see 05-repartition-taches.md §4.
 * Each dev edits only the body of their own method, so Git never sees a conflict on the same lines
 * here.
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConf {
  private final RestAuthenticationEntryPoint entryPoint;
  private final RestAccessDeniedHandler accessDeniedHandler;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      BearerAuthFilter bearerAuthFilter,
      SelfAuthorizationManager selfAuthorizationManager)
      throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
        .exceptionHandling(
            e -> e.authenticationEntryPoint(entryPoint).accessDeniedHandler(accessDeniedHandler))
        .authorizeHttpRequests(
            auth -> {
              auth.requestMatchers("/login", "/ping").permitAll();
              identityAndAcademicStructureMatchers(auth, selfAuthorizationManager); // Dev 1
              gradesAndTranscriptMatchers(auth, selfAuthorizationManager); // Dev 2
              auth.anyRequest().authenticated();
            })
        .addFilterBefore(bearerAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  /**
   * Dev 1 (Task 1.1/1.2/1.3): users, tracks, semesters, teaching-units, courses, class-groups,
   * academic-years, group-assignment, teacher-assignment.
   */
  private void identityAndAcademicStructureMatchers(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth,
      AuthorizationManager<RequestAuthorizationContext> selfAuthorizationManager) {
    // TODO(Dev 1): add requestMatchers(...) rules here.
  }

  /** Dev 2 (Task 2.1/2.3/2.4): exams, grades, transcript, promotions/diploma. */
  private void gradesAndTranscriptMatchers(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth,
      AuthorizationManager<RequestAuthorizationContext> selfAuthorizationManager) {
    // TODO(Dev 2): add requestMatchers(...) rules here.
  }
}
