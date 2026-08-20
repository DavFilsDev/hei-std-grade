package school.hei.stdgrade.it;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static school.hei.stdgrade.conf.TestUtils.ADMIN_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.STUDENT_A_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.STUDENT_B_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.TEACHER_A_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.TEACHER_B_EMAIL;
import static school.hei.stdgrade.conf.TestUtils.authHeaders;
import static school.hei.stdgrade.conf.TestUtils.jsonHeaders;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import school.hei.stdgrade.conf.TestcontainersConfigurer;
import school.hei.stdgrade.model.CorrectGradePayload;
import school.hei.stdgrade.model.Exam;
import school.hei.stdgrade.model.Grade;
import school.hei.stdgrade.model.SessionType;

class ExamAndGradeRbacIT extends TestcontainersConfigurer {
  @Autowired TestRestTemplate testRestTemplate;

  private String adminToken;
  private String teacherAToken;
  private String teacherBToken;
  private String studentAToken;
  private String studentBToken;

  private static final String COURSE_ID = "course-donnees1";
  private static final String ACADEMIC_YEAR_ID = "academic-year-2024";
  private static final String STUDENT_A_ID = "user-student-a";
  private static final String STUDENT_B_ID = "user-student-b";

  @BeforeEach
  void setUp() {
    adminToken = utils.tokenOf(ADMIN_EMAIL);
    teacherAToken = utils.tokenOf(TEACHER_A_EMAIL);
    teacherBToken = utils.tokenOf(TEACHER_B_EMAIL);
    studentAToken = utils.tokenOf(STUDENT_A_EMAIL);
    studentBToken = utils.tokenOf(STUDENT_B_EMAIL);
  }

  @Test
  void everyone_can_list_exams_without_authentication() {
    var response =
        testRestTemplate.exchange(
            "/courses/{courseId}/academic-years/{academicYearId}/exams",
            GET,
            new HttpEntity<>(jsonHeaders()),
            Exam[].class,
            COURSE_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(OK, response.getStatusCode());
  }

  @Test
  void admin_can_crupdate_an_exam() {
    var toCreate = exam(randomUUID().toString());

    var response =
        testRestTemplate.exchange(
            "/courses/{courseId}/academic-years/{academicYearId}/exams",
            PUT,
            new HttpEntity<>(toCreate, authHeaders(adminToken)),
            Exam.class,
            COURSE_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(toCreate.examDate(), response.getBody().examDate());
    assertEquals(toCreate.coefficient(), response.getBody().coefficient());
    assertEquals(toCreate.sessionType(), response.getBody().sessionType());
    assertEquals(COURSE_ID, response.getBody().courseId());
    assertEquals(ACADEMIC_YEAR_ID, response.getBody().academicYearId());
  }

  @Test
  void assigned_teacher_can_crupdate_an_exam() {
    var toCreate = exam(randomUUID().toString());

    var response =
        testRestTemplate.exchange(
            "/courses/{courseId}/academic-years/{academicYearId}/exams",
            PUT,
            new HttpEntity<>(toCreate, authHeaders(teacherAToken)),
            Exam.class,
            COURSE_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void unassigned_teacher_cannot_crupdate_an_exam() {
    var toCreate = exam(randomUUID().toString());

    var response =
        testRestTemplate.exchange(
            "/courses/{courseId}/academic-years/{academicYearId}/exams",
            PUT,
            new HttpEntity<>(toCreate, authHeaders(teacherBToken)),
            Map.class,
            COURSE_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void student_cannot_crupdate_an_exam() {
    var toCreate = exam(randomUUID().toString());

    var response =
        testRestTemplate.exchange(
            "/courses/{courseId}/academic-years/{academicYearId}/exams",
            PUT,
            new HttpEntity<>(toCreate, authHeaders(studentAToken)),
            Map.class,
            COURSE_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void anonymous_cannot_crupdate_an_exam() {
    var toCreate = exam(randomUUID().toString());

    var response =
        testRestTemplate.exchange(
            "/courses/{courseId}/academic-years/{academicYearId}/exams",
            PUT,
            new HttpEntity<>(toCreate, jsonHeaders()),
            Map.class,
            COURSE_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  void everyone_can_get_exam_by_id() {
    var toCreate = exam(randomUUID().toString());
    var createResponse =
        testRestTemplate.exchange(
            "/courses/{courseId}/academic-years/{academicYearId}/exams",
            PUT,
            new HttpEntity<>(toCreate, authHeaders(adminToken)),
            Exam.class,
            COURSE_ID,
            ACADEMIC_YEAR_ID);

    var response =
        testRestTemplate.exchange(
            "/exams/{examId}",
            GET,
            new HttpEntity<>(jsonHeaders()),
            Exam.class,
            createResponse.getBody().id());

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void creating_exam_without_coefficient_for_regular_returns_bad_request() {
    var toCreate =
        Exam.builder()
            .id(randomUUID().toString())
            .examDate(now())
            .coefficient(null) // Missing coefficient for REGULAR
            .sessionType(SessionType.REGULAR)
            .build();

    var response =
        testRestTemplate.exchange(
            "/courses/{courseId}/academic-years/{academicYearId}/exams",
            PUT,
            new HttpEntity<>(toCreate, authHeaders(adminToken)),
            Map.class,
            COURSE_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void creating_exam_with_coefficient_for_retake_returns_bad_request() {
    var toCreate =
        Exam.builder()
            .id(randomUUID().toString())
            .examDate(now())
            .coefficient(0.5) // Coefficient not allowed for RETAKE
            .sessionType(SessionType.RETAKE)
            .build();

    var response =
        testRestTemplate.exchange(
            "/courses/{courseId}/academic-years/{academicYearId}/exams",
            PUT,
            new HttpEntity<>(toCreate, authHeaders(adminToken)),
            Map.class,
            COURSE_ID,
            ACADEMIC_YEAR_ID);

    assertEquals(BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void admin_can_create_a_grade() {
    var exam = createExam();
    var toCreate =
        Grade.builder()
            .id(randomUUID().toString())
            .studentId(STUDENT_A_ID)
            .examId(exam.id())
            .score(15.5)
            .build();

    var response =
        testRestTemplate.exchange(
            "/grades", PUT, new HttpEntity<>(toCreate, authHeaders(adminToken)), Grade.class);

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(toCreate.score(), response.getBody().score());
  }

  @Test
  void assigned_teacher_can_create_a_grade() {
    var exam = createExam();
    var toCreate =
        Grade.builder()
            .id(randomUUID().toString())
            .studentId(STUDENT_A_ID)
            .examId(exam.id())
            .score(14.0)
            .build();

    var response =
        testRestTemplate.exchange(
            "/grades", PUT, new HttpEntity<>(toCreate, authHeaders(teacherAToken)), Grade.class);

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void unassigned_teacher_cannot_create_a_grade() {
    var exam = createExam();
    var toCreate =
        Grade.builder()
            .id(randomUUID().toString())
            .studentId(STUDENT_A_ID)
            .examId(exam.id())
            .score(12.0)
            .build();

    var response =
        testRestTemplate.exchange(
            "/grades", PUT, new HttpEntity<>(toCreate, authHeaders(teacherBToken)), Map.class);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void student_cannot_create_a_grade() {
    var exam = createExam();
    var toCreate =
        Grade.builder()
            .id(randomUUID().toString())
            .studentId(STUDENT_A_ID)
            .examId(exam.id())
            .score(16.0)
            .build();

    var response =
        testRestTemplate.exchange(
            "/grades", PUT, new HttpEntity<>(toCreate, authHeaders(studentAToken)), Map.class);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void admin_can_correct_a_grade_with_reason() {
    var grade = createGrade();

    var payload = new CorrectGradePayload(18.5, "Erreur de saisie");

    var response =
        testRestTemplate.exchange(
            "/grades/{gradeId}",
            PATCH,
            new HttpEntity<>(payload, authHeaders(adminToken)),
            Grade.class,
            grade.id());

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(18.5, response.getBody().score());
  }

  @Test
  void correcting_a_grade_without_reason_returns_bad_request() {
    var grade = createGrade();

    var payload = new CorrectGradePayload(17.0, null);

    var response =
        testRestTemplate.exchange(
            "/grades/{gradeId}",
            PATCH,
            new HttpEntity<>(payload, authHeaders(adminToken)),
            Map.class,
            grade.id());

    assertEquals(BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void assigned_teacher_can_correct_a_grade() {
    var grade = createGrade();

    var payload = new CorrectGradePayload(16.5, "Réclamation étudiante");

    var response =
        testRestTemplate.exchange(
            "/grades/{gradeId}",
            PATCH,
            new HttpEntity<>(payload, authHeaders(teacherAToken)),
            Grade.class,
            grade.id());

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void unassigned_teacher_cannot_correct_a_grade() {
    var grade = createGrade();

    var payload = new CorrectGradePayload(15.0, "Test");

    var response =
        testRestTemplate.exchange(
            "/grades/{gradeId}",
            PATCH,
            new HttpEntity<>(payload, authHeaders(teacherBToken)),
            Map.class,
            grade.id());

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void admin_can_view_grade_history() {
    var grade = createGrade();

    var payload = new CorrectGradePayload(19.0, "Correction");
    testRestTemplate.exchange(
        "/grades/{gradeId}",
        PATCH,
        new HttpEntity<>(payload, authHeaders(adminToken)),
        Grade.class,
        grade.id());

    var response =
        testRestTemplate.exchange(
            "/grades/{gradeId}/history",
            GET,
            new HttpEntity<>(authHeaders(adminToken)),
            Object[].class,
            grade.id());

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().length > 0);
  }

  @Test
  void assigned_teacher_can_view_grade_history() {
    var grade = createGrade();

    var response =
        testRestTemplate.exchange(
            "/grades/{gradeId}/history",
            GET,
            new HttpEntity<>(authHeaders(teacherAToken)),
            Object[].class,
            grade.id());

    assertEquals(OK, response.getStatusCode());
  }

  @Test
  void unassigned_teacher_cannot_view_grade_history() {
    var grade = createGrade();

    var response =
        testRestTemplate.exchange(
            "/grades/{gradeId}/history",
            GET,
            new HttpEntity<>(authHeaders(teacherBToken)),
            Map.class,
            grade.id());

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void student_can_view_their_own_grades() {
    var exam = createExam();
    var grade = createGradeForStudent(exam.id(), STUDENT_A_ID);

    var response =
        testRestTemplate.exchange(
            "/students/{studentId}/grades",
            GET,
            new HttpEntity<>(authHeaders(studentAToken)),
            Grade[].class,
            STUDENT_A_ID);

    assertEquals(OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().length > 0);
  }

  @Test
  void student_cannot_view_another_students_grades() {
    var response =
        testRestTemplate.exchange(
            "/students/{studentId}/grades",
            GET,
            new HttpEntity<>(authHeaders(studentAToken)),
            Map.class,
            STUDENT_B_ID);

    assertEquals(FORBIDDEN, response.getStatusCode());
  }

  @Test
  void teacher_can_view_any_students_grades() {
    var response =
        testRestTemplate.exchange(
            "/students/{studentId}/grades",
            GET,
            new HttpEntity<>(authHeaders(teacherAToken)),
            Grade[].class,
            STUDENT_A_ID);

    assertEquals(OK, response.getStatusCode());
  }

  @Test
  void admin_can_view_any_students_grades() {
    var response =
        testRestTemplate.exchange(
            "/students/{studentId}/grades",
            GET,
            new HttpEntity<>(authHeaders(adminToken)),
            Grade[].class,
            STUDENT_A_ID);

    assertEquals(OK, response.getStatusCode());
  }

  private Exam createExam() {
    var toCreate = exam(randomUUID().toString());

    var response =
        testRestTemplate.exchange(
            "/courses/{courseId}/academic-years/{academicYearId}/exams",
            PUT,
            new HttpEntity<>(toCreate, authHeaders(adminToken)),
            Exam.class,
            COURSE_ID,
            ACADEMIC_YEAR_ID);

    return response.getBody();
  }

  private Grade createGrade() {
    return createGradeForStudent(createExam().id(), STUDENT_A_ID);
  }

  private Grade createGradeForStudent(String examId, String studentId) {
    var toCreate =
        Grade.builder()
            .id(randomUUID().toString())
            .studentId(studentId)
            .examId(examId)
            .score(14.0)
            .build();

    var response =
        testRestTemplate.exchange(
            "/grades", PUT, new HttpEntity<>(toCreate, authHeaders(adminToken)), Grade.class);

    return response.getBody();
  }

  private static Exam exam(String id) {
    return Exam.builder()
        .id(id)
        .examDate(now())
        .coefficient(0.5)
        .sessionType(SessionType.REGULAR)
        .build();
  }
}
