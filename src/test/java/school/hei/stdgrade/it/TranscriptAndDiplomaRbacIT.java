package school.hei.stdgrade.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static school.hei.stdgrade.conf.TestUtils.ADMIN_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.STUDENT_A_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.STUDENT_B_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.TEACHER_A_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.authHeaders;
import static school.hei.stdgrade.conf.TestUtils.jsonHeaders;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import school.hei.stdgrade.conf.TestcontainersConfigurer;
import school.hei.stdgrade.model.DiplomaStatus;
import school.hei.stdgrade.model.TranscriptSummary;

class TranscriptAndDiplomaRbacIT extends TestcontainersConfigurer {
  @Autowired TestRestTemplate testRestTemplate;

  private static final String STUDENT_A_ID = "user-student-a";
  private static final String STUDENT_B_ID = "user-student-b";
  private static final String ACADEMIC_YEAR_ID = "academic-year-2024";

  private String adminToken;
  private String teacherAToken;
  private String studentAToken;
  private String studentBToken;

  @BeforeEach
  void setUp() {
    adminToken = utils.tokenOf(ADMIN_EMAIL);
    teacherAToken = utils.tokenOf(TEACHER_A_EMAIL);
    studentAToken = utils.tokenOf(STUDENT_A_EMAIL);
    studentBToken = utils.tokenOf(STUDENT_B_EMAIL);
  }

  @Test
  void student_can_get_own_transcript_summary() {
    var response =
        testRestTemplate.exchange(
            "/students/{studentId}/academic-years/{academicYearId}/transcript-summary",
            GET,
            new HttpEntity<>(authHeaders(studentAToken)),
            TranscriptSummary.class,
            STUDENT_A_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(STUDENT_A_ID, response.getBody().studentId());
  }

  @Test
  void student_cannot_get_another_students_transcript_summary() {
    var response =
        testRestTemplate.exchange(
            "/students/{studentId}/academic-years/{academicYearId}/transcript-summary",
            GET,
            new HttpEntity<>(authHeaders(studentBToken)),
            Map.class,
            STUDENT_A_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void teacher_alone_cannot_get_a_students_transcript_summary() {
    var response =
        testRestTemplate.exchange(
            "/students/{studentId}/academic-years/{academicYearId}/transcript-summary",
            GET,
            new HttpEntity<>(authHeaders(teacherAToken)),
            Map.class,
            STUDENT_A_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void admin_can_get_any_students_transcript_summary() {
    var response =
        testRestTemplate.exchange(
            "/students/{studentId}/academic-years/{academicYearId}/transcript-summary",
            GET,
            new HttpEntity<>(authHeaders(adminToken)),
            TranscriptSummary.class,
            STUDENT_A_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(OK, response.getStatusCode());
  }

  @Test
  void anonymous_cannot_get_a_transcript_summary() {
    var response =
        testRestTemplate.exchange(
            "/students/{studentId}/academic-years/{academicYearId}/transcript-summary",
            GET,
            new HttpEntity<>(jsonHeaders()),
            Map.class,
            STUDENT_A_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  void student_can_request_generation_of_own_transcript() {
    var response =
        testRestTemplate.exchange(
            "/students/{studentId}/academic-years/{academicYearId}/transcript-requests",
            POST,
            new HttpEntity<>(authHeaders(studentAToken)),
            Void.class,
            STUDENT_A_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(ACCEPTED, response.getStatusCode());
  }

  @Test
  void student_cannot_request_generation_of_another_students_transcript() {
    var response =
        testRestTemplate.exchange(
            "/students/{studentId}/academic-years/{academicYearId}/transcript-requests",
            POST,
            new HttpEntity<>(authHeaders(studentBToken)),
            Map.class,
            STUDENT_A_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void teacher_and_admin_can_list_promotions() {
    var teacherResponse =
        testRestTemplate.exchange(
            "/promotions", GET, new HttpEntity<>(authHeaders(teacherAToken)), Integer[].class);
    var adminResponse =
        testRestTemplate.exchange(
            "/promotions", GET, new HttpEntity<>(authHeaders(adminToken)), Integer[].class);

    assertEquals(OK, teacherResponse.getStatusCode());
    assertEquals(OK, adminResponse.getStatusCode());
  }

  @Test
  void student_cannot_list_promotions() {
    var response =
        testRestTemplate.exchange(
            "/promotions", GET, new HttpEntity<>(authHeaders(studentAToken)), Map.class);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void only_admin_can_list_graduates_of_a_promotion() {
    var adminResponse =
        testRestTemplate.exchange(
            "/promotions/{promotionYear}/graduates",
            GET,
            new HttpEntity<>(authHeaders(adminToken)),
            Object[].class,
            2024);
    var teacherResponse =
        testRestTemplate.exchange(
            "/promotions/{promotionYear}/graduates",
            GET,
            new HttpEntity<>(authHeaders(teacherAToken)),
            Map.class,
            2024);

    assertEquals(OK, adminResponse.getStatusCode());
    assertEquals(FORBIDDEN, teacherResponse.getStatusCode());
  }

  @Test
  void student_can_get_own_diploma_status() {
    var response =
        testRestTemplate.exchange(
            "/students/{studentId}/diploma-status",
            GET,
            new HttpEntity<>(authHeaders(studentAToken)),
            DiplomaStatus.class,
            STUDENT_A_ID);

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void student_cannot_get_another_students_diploma_status() {
    var response =
        testRestTemplate.exchange(
            "/students/{studentId}/diploma-status",
            GET,
            new HttpEntity<>(authHeaders(studentBToken)),
            Map.class,
            STUDENT_A_ID);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }
}
