package school.hei.stdgrade.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static school.hei.stdgrade.conf.TestUtils.ACADEMIC_TRACKS_URL;
import static school.hei.stdgrade.conf.TestUtils.ADMIN_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.STUDENT_A_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.TEACHER_A_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.authHeaders;
import static school.hei.stdgrade.conf.TestUtils.jsonHeaders;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import school.hei.stdgrade.conf.TestcontainersConfigurer;
import school.hei.stdgrade.model.AcademicTrack;

class AcademicTrackRbacIT extends TestcontainersConfigurer {

  @Autowired TestRestTemplate testRestTemplate;

  private String adminToken;
  private String teacherToken;
  private String studentToken;

  @BeforeEach
  void setUp() {
    adminToken = utils.tokenOf(ADMIN_EMAIL);
    teacherToken = utils.tokenOf(TEACHER_A_EMAIL);
    studentToken = utils.tokenOf(STUDENT_A_EMAIL);
  }

  @Test
  void everyone_can_list_academic_tracks_without_authentication() {
    var response =
        testRestTemplate.exchange(
            ACADEMIC_TRACKS_URL, GET, new HttpEntity<>(jsonHeaders()), AcademicTrack[].class);

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void admin_can_crupdate_an_academic_track() {
    var toCreate = academicTrack();

    var response =
        testRestTemplate.exchange(
            ACADEMIC_TRACKS_URL,
            PUT,
            new HttpEntity<>(toCreate, authHeaders(adminToken)),
            AcademicTrack.class);

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(toCreate.code(), response.getBody().code());
  }

  @Test
  void teacher_cannot_crupdate_an_academic_track() {
    var toCreate = academicTrack();

    var response =
        testRestTemplate.exchange(
            ACADEMIC_TRACKS_URL,
            PUT,
            new HttpEntity<>(toCreate, authHeaders(teacherToken)),
            Map.class);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void student_cannot_crupdate_an_academic_track() {
    var toCreate = academicTrack();

    var response =
        testRestTemplate.exchange(
            ACADEMIC_TRACKS_URL,
            PUT,
            new HttpEntity<>(toCreate, authHeaders(studentToken)),
            Map.class);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void anonymous_cannot_crupdate_an_academic_track() {
    var toCreate = academicTrack();

    var response =
        testRestTemplate.exchange(
            ACADEMIC_TRACKS_URL, PUT, new HttpEntity<>(toCreate, jsonHeaders()), Map.class);

    assertEquals(UNAUTHORIZED, response.getStatusCode());
  }

  private static AcademicTrack academicTrack() {
    var id = UUID.randomUUID().toString();
    var code = id.substring(0, 2).toUpperCase();
    if (!code.equals("TN") && !code.equals("EL")) {
      code = "TN";
    }
    return AcademicTrack.builder().id(id).code(code).name("Track " + code).build();
  }
}
