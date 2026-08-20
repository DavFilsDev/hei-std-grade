package school.hei.stdgrade.security;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static school.hei.stdgrade.security.model.UserRole.ADMIN;
import static school.hei.stdgrade.security.model.UserRole.TEACHER;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
@Slf4j
public class SecurityConf {

  private final RestAuthenticationEntryPoint entryPoint;
  private final RestAccessDeniedHandler accessDeniedHandler;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
  }

  @Bean
  public FilterRegistrationBean<BearerAuthFilter> disableBearerAuthFilterAutoRegistration(
      BearerAuthFilter bearerAuthFilter) {
    var registration = new FilterRegistrationBean<>(bearerAuthFilter);
    registration.setEnabled(false);
    return registration;
  }

  @Bean
  @Order(1)
  public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
    log.info("=== Configuring webFilterChain for /web/** ===");
    return http.securityMatcher("/web/**")
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/web/login")
                    .permitAll()
                    .requestMatchers("/web/**")
                    .hasRole(ADMIN.name()))
        .formLogin(
            form -> form.loginPage("/web/login").loginProcessingUrl("/web/login").permitAll())
        .build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain apiFilterChain(
      HttpSecurity http,
      BearerAuthFilter bearerAuthFilter,
      SelfAuthorizationManager selfAuthorizationManager)
      throws Exception {
    log.info("=== Configuring apiFilterChain for /** (rest) ===");
    return http.securityMatcher("/**")
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
        .exceptionHandling(
            e -> e.authenticationEntryPoint(entryPoint).accessDeniedHandler(accessDeniedHandler))
        .authorizeHttpRequests(
            auth -> {
              auth.requestMatchers("/error").permitAll();
              auth.requestMatchers("/login", "/ping").permitAll();
              auth.requestMatchers(GET, "/css/**").permitAll();
              identityAndAcademicStructureMatchers(auth, selfAuthorizationManager);
              gradesAndTranscriptMatchers(auth, selfAuthorizationManager);

              identityAndAcademicStructureMatchers(auth, selfAuthorizationManager);
              gradesAndTranscriptMatchers(auth, selfAuthorizationManager);
              auth.anyRequest().authenticated();
            })
        .addFilterBefore(bearerAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  private void identityAndAcademicStructureMatchers(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth,
      AuthorizationManager<RequestAuthorizationContext> selfAuthorizationManager) {

    auth.requestMatchers(GET, "/roles").permitAll();

    auth.requestMatchers(GET, "/users").hasRole(ADMIN.name());
    auth.requestMatchers(GET, "/users/{userId}").access(selfAuthorizationManager);
    auth.requestMatchers(PUT, "/users").hasRole(ADMIN.name());
    auth.requestMatchers(PUT, "/users/{userId}/track").hasRole(ADMIN.name());

    auth.requestMatchers(GET, "/academic-tracks").permitAll();
    auth.requestMatchers(PUT, "/academic-tracks").hasRole(ADMIN.name());
    auth.requestMatchers(PUT, "/academic-tracks/{trackId}").hasRole(ADMIN.name());

    auth.requestMatchers(GET, "/semesters").permitAll();
    auth.requestMatchers(PUT, "/semesters").hasRole(ADMIN.name());

    auth.requestMatchers(GET, "/teaching-units").permitAll();
    auth.requestMatchers(PUT, "/teaching-units").hasRole(ADMIN.name());

    auth.requestMatchers(GET, "/courses").permitAll();
    auth.requestMatchers(GET, "/courses/{courseId}").permitAll();
    auth.requestMatchers(GET, "/courses/{courseId}/coefficient-status").permitAll();
    auth.requestMatchers(GET, "/teaching-units/{teachingUnitId}/courses").permitAll();

    auth.requestMatchers(PUT, "/courses").hasRole(ADMIN.name());
    auth.requestMatchers(PUT, "/courses/{courseId}").hasRole(ADMIN.name());
    auth.requestMatchers(PUT, "/teaching-units/{teachingUnitId}/courses").hasRole(ADMIN.name());

    auth.requestMatchers(GET, "/class-groups").hasAnyRole(ADMIN.name(), TEACHER.name());
    auth.requestMatchers(PUT, "/class-groups").hasRole(ADMIN.name());

    auth.requestMatchers(GET, "/academic-years").permitAll();
    auth.requestMatchers(PUT, "/academic-years").hasRole(ADMIN.name());
    auth.requestMatchers(PUT, "/academic-years/{academicYearId}").hasRole(ADMIN.name());

    auth.requestMatchers(
            GET, "/students/{studentId}/academic-years/{academicYearId}/group-assignment")
        .access(selfAuthorizationManager);
    auth.requestMatchers(
            PUT, "/students/{studentId}/academic-years/{academicYearId}/group-assignment")
        .hasRole(ADMIN.name());
    auth.requestMatchers(
            DELETE, "/students/{studentId}/academic-years/{academicYearId}/group-assignment")
        .hasRole(ADMIN.name());
    auth.requestMatchers(GET, "/academic-years/{academicYearId}/groups/{groupId}/students")
        .hasAnyRole(ADMIN.name(), TEACHER.name());

    auth.requestMatchers(GET, "/courses/{courseId}/academic-years/{academicYearId}/teachers")
        .permitAll();
    auth.requestMatchers(PUT, "/courses/{courseId}/academic-years/{academicYearId}/teachers")
        .hasRole(ADMIN.name());
    auth.requestMatchers(
            DELETE, "/teachers/{teacherId}/courses/{courseId}/academic-years/{academicYearId}")
        .hasRole(ADMIN.name());
    auth.requestMatchers(GET, "/teachers/{teacherId}/academic-years/{academicYearId}/courses")
        .access(selfAuthorizationManager);
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

    auth.requestMatchers(
            GET, "/students/{studentId}/academic-years/{academicYearId}/transcript-summary")
        .access(selfAuthorizationManager);
    auth.requestMatchers(
            POST, "/students/{studentId}/academic-years/{academicYearId}/transcript-requests")
        .access(selfAuthorizationManager);

    auth.requestMatchers(GET, "/promotions").hasAnyRole(TEACHER.name(), ADMIN.name());
    auth.requestMatchers(GET, "/promotions/{promotionYear}/graduates").hasRole(ADMIN.name());
    auth.requestMatchers(GET, "/promotions/{promotionYear}/graduates.xlsx").hasRole(ADMIN.name());
    auth.requestMatchers(GET, "/students/{studentId}/diploma-status")
        .access(selfAuthorizationManager);
  }
}
