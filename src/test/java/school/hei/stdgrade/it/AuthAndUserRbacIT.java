package school.hei.stdgrade.it;

import static java.time.LocalDate.now;
import static java.util.List.of;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static school.hei.stdgrade.conf.TestUtils.ADMIN_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.LOGIN_URL;
import static school.hei.stdgrade.conf.TestUtils.ROLES_URL;
import static school.hei.stdgrade.conf.TestUtils.SEEDED_PASSWORD;
import static school.hei.stdgrade.conf.TestUtils.STUDENT_A_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.STUDENT_B_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.TEACHER_A_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.USERS_URL;
import static school.hei.stdgrade.conf.TestUtils.USER_BY_ID_URL;
import static school.hei.stdgrade.conf.TestUtils.USER_TRACK_URL;
import static school.hei.stdgrade.conf.TestUtils.authHeaders;
import static school.hei.stdgrade.conf.TestUtils.jsonHeaders;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import school.hei.stdgrade.conf.TestcontainersConfigurer;
import school.hei.stdgrade.model.AssignTrackPayload;
import school.hei.stdgrade.model.CrupdateUserPayload;
import school.hei.stdgrade.model.LoginPayload;
import school.hei.stdgrade.model.Role;
import school.hei.stdgrade.model.RoleName;
import school.hei.stdgrade.model.User;
import school.hei.stdgrade.model.UserWithToken;

class AuthAndUserRbacIT extends TestcontainersConfigurer {
  @Autowired TestRestTemplate testRestTemplate;

  private String adminToken;
  private String teacherToken;
  private String studentAToken;

  @BeforeEach
  void setUp() {
    adminToken = utils.tokenOf(ADMIN_EMAIL);
    teacherToken = utils.tokenOf(TEACHER_A_EMAIL);
    studentAToken = utils.tokenOf(STUDENT_A_EMAIL);
  }

  @Test
  void seeded_admin_can_login() {
    var response =
        testRestTemplate.postForEntity(
            LOGIN_URL, new LoginPayload(ADMIN_EMAIL, SEEDED_PASSWORD), UserWithToken.class);

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().token());
    assertEquals(ADMIN_EMAIL, response.getBody().email());
  }

  @Test
  void login_with_wrong_password_is_unauthorized() {
    var response =
        testRestTemplate.postForEntity(
            LOGIN_URL, new LoginPayload(ADMIN_EMAIL, "wrong-password"), Map.class);

    assertEquals(UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  void everyone_can_list_roles_without_authentication() {
    var response =
        testRestTemplate.exchange(ROLES_URL, GET, new HttpEntity<>(jsonHeaders()), Role[].class);

    assertEquals(OK, response.getStatusCode());
    assertEquals(3, response.getBody().length);
  }

  @Test
  void admin_can_list_all_users() {
    var response =
        testRestTemplate.exchange(
            USERS_URL, GET, new HttpEntity<>(authHeaders(adminToken)), User[].class);

    assertEquals(OK, response.getStatusCode());
    assertTrue(response.getBody().length >= 6);
  }

  @Test
  void teacher_cannot_list_all_users() {
    var response =
        testRestTemplate.exchange(
            USERS_URL, GET, new HttpEntity<>(authHeaders(teacherToken)), Map.class);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void anonymous_cannot_list_all_users() {
    var response =
        testRestTemplate.exchange(USERS_URL, GET, new HttpEntity<>(jsonHeaders()), Map.class);

    assertEquals(UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  void admin_can_create_a_user_with_a_role_and_password() {
    var id = randomUUID().toString();
    var toCreate =
        CrupdateUserPayload.builder()
            .ref("STD24" + id.substring(0, 3))
            .lastName("Doe")
            .firstName("Jane")
            .email("jane-" + id + "@hei-std-grade.test")
            .password("SomePass123!")
            .isEnabled(true)
            .entranceDate(now())
            .roles(of(RoleName.ROLE_STUDENT))
            .build();

    var response =
        testRestTemplate.exchange(
            USERS_URL, PUT, new HttpEntity<>(toCreate, authHeaders(adminToken)), User.class);

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().id());
    assertEquals(toCreate.email(), response.getBody().email());
    assertEquals(of(RoleName.ROLE_STUDENT), response.getBody().roles());
  }

  @Test
  void creating_a_user_without_password_is_a_bad_request() {
    var id = randomUUID().toString();
    var toCreate =
        CrupdateUserPayload.builder()
            .ref("STD25" + id.substring(0, 3))
            .lastName("Doe")
            .firstName("John")
            .email("john-" + id + "@hei-std-grade.test")
            .isEnabled(true)
            .entranceDate(now())
            .roles(of(RoleName.ROLE_STUDENT))
            .build();

    var response =
        testRestTemplate.exchange(
            USERS_URL, PUT, new HttpEntity<>(toCreate, authHeaders(adminToken)), Map.class);

    assertEquals(BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void teacher_cannot_create_a_user() {
    var id = randomUUID().toString();
    var toCreate =
        CrupdateUserPayload.builder()
            .ref("STD26" + id.substring(0, 3))
            .lastName("Doe")
            .firstName("Jack")
            .email("jack-" + id + "@hei-std-grade.test")
            .password("SomePass123!")
            .isEnabled(true)
            .entranceDate(now())
            .roles(of(RoleName.ROLE_STUDENT))
            .build();

    var response =
        testRestTemplate.exchange(
            USERS_URL, PUT, new HttpEntity<>(toCreate, authHeaders(teacherToken)), Map.class);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void a_student_can_read_their_own_user() {
    var response =
        testRestTemplate.exchange(
            USER_BY_ID_URL,
            GET,
            new HttpEntity<>(authHeaders(studentAToken)),
            User.class,
            studentUserId());

    assertEquals(OK, response.getStatusCode());
    assertEquals(STUDENT_A_EMAIL, response.getBody().email());
  }

  @Test
  void admin_can_read_any_user() {
    var response =
        testRestTemplate.exchange(
            USER_BY_ID_URL,
            GET,
            new HttpEntity<>(authHeaders(adminToken)),
            User.class,
            studentUserId());

    assertEquals(OK, response.getStatusCode());
  }

  @Test
  void a_student_cannot_read_another_students_user() {
    var studentBToken = utils.tokenOf(STUDENT_B_EMAIL);

    var response =
        testRestTemplate.exchange(
            USER_BY_ID_URL,
            GET,
            new HttpEntity<>(authHeaders(studentBToken)),
            Map.class,
            studentUserId());

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void admin_can_assign_a_track_to_a_student_without_one() {
    var studentBId = getUserId(STUDENT_B_EMAIL, adminToken);

    var response =
        testRestTemplate.exchange(
            USER_TRACK_URL,
            PUT,
            new HttpEntity<>(new AssignTrackPayload("track-el"), authHeaders(adminToken)),
            User.class,
            studentBId);

    assertEquals(OK, response.getStatusCode());
    assertEquals("track-el", response.getBody().trackId());
  }

  @Test
  void assigning_a_track_twice_is_a_bad_request() {
    var response =
        testRestTemplate.exchange(
            USER_TRACK_URL,
            PUT,
            new HttpEntity<>(new AssignTrackPayload("track-el"), authHeaders(adminToken)),
            Map.class,
            studentUserId());

    assertEquals(BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void teacher_cannot_assign_a_track() {
    var response =
        testRestTemplate.exchange(
            USER_TRACK_URL,
            PUT,
            new HttpEntity<>(new AssignTrackPayload("track-el"), authHeaders(teacherToken)),
            Map.class,
            studentUserId());

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  private String studentUserId() {
    return getUserId(STUDENT_A_EMAIL, adminToken);
  }

  private String getUserId(String email, String requesterToken) {
    var response =
        testRestTemplate.exchange(
            USERS_URL, GET, new HttpEntity<>(authHeaders(requesterToken)), User[].class);
    for (User user : response.getBody()) {
      if (user.email().equals(email)) {
        return user.id();
      }
    }
    throw new IllegalStateException("Seeded user not found: " + email);
  }
}
