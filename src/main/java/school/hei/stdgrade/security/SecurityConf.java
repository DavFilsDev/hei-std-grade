package school.hei.stdgrade.security;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static school.hei.stdgrade.security.model.UserRole.ADMIN;
import static school.hei.stdgrade.security.model.UserRole.TEACHER;

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

  private void identityAndAcademicStructureMatchers(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth,
      AuthorizationManager<RequestAuthorizationContext> selfAuthorizationManager) {

    auth.requestMatchers(GET, "/roles").permitAll();

    auth.requestMatchers(PUT, "/users").hasRole(ADMIN.name());

    auth.requestMatchers(PUT, "/users/{userId}/track").hasRole(ADMIN.name());

    auth.requestMatchers(GET, "/users").hasRole(ADMIN.name());

    auth.requestMatchers(GET, "/users/{userId}").access(selfAuthorizationManager);
  }

  private void gradesAndTranscriptMatchers(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth,
      AuthorizationManager<RequestAuthorizationContext> selfAuthorizationManager) {
    auth.requestMatchers(GET, "/courses/{courseId}/academic-years/{academicYearId}/exams")
        .permitAll();
    auth.requestMatchers(PUT, "/courses/{courseId}/academic-years/{academicYearId}/exams")
        .hasAnyRole(TEACHER.name(), ADMIN.name());
    auth.requestMatchers(GET, "/exams/{examId}").permitAll();
    auth.requestMatchers(PUT, "/grades").hasAnyRole(TEACHER.name(), ADMIN.name());
    auth.requestMatchers(PATCH, "/grades/{gradeId}").hasAnyRole(TEACHER.name(), ADMIN.name());
    auth.requestMatchers(GET, "/grades/{gradeId}/history").hasAnyRole(TEACHER.name(), ADMIN.name());
    auth.requestMatchers(GET, "/students/{studentId}/grades").access(selfAuthorizationManager);
  }
}
