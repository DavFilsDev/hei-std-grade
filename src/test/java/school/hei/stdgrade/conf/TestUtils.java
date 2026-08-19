package school.hei.stdgrade.conf;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.AllArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import school.hei.stdgrade.model.LoginPayload;
import school.hei.stdgrade.model.UserWithToken;

// URLs and payload/response types below are placeholders until doc/api.yml is merged (Task 0 -
// Personne 2); UserWithToken/LoginPayload references are added once the OpenAPI-generated (or
// hand-written) model types exist. Seed emails match db/testdata/V2_1__test_create_users.sql.
@AllArgsConstructor
public class TestUtils {
  public static final String ADMIN_EMAIL = "admin@hei-std-grade.test";
  public static final String TEACHER_A_EMAIL = "teacher-a@hei-std-grade.test";
  public static final String TEACHER_B_EMAIL = "teacher-b@hei-std-grade.test";
  public static final String STUDENT_A_EMAIL = "student-a@hei-std-grade.test";
  public static final String STUDENT_B_EMAIL = "student-b@hei-std-grade.test";
  public static final String STUDENT_C_EMAIL = "student-c@hei-std-grade.test";
  public static final String SEEDED_PASSWORD = "AdminPass123!";

  public static final String LOGIN_URL = "/login";
  public static final String ROLES_URL = "/roles";

  public static final String USERS_URL = "/users";
  public static final String USER_BY_ID_URL = "/users/{userId}";
  public static final String USER_TRACK_URL = "/users/{userId}/track";

  public static final String ACADEMIC_TRACKS_URL = "/academic-tracks";
  public static final String SEMESTERS_URL = "/semesters";
  public static final String TEACHING_UNITS_URL = "/teaching-units";
  public static final String TEACHING_UNIT_COURSES_URL = "/teaching-units/{teachingUnitId}/courses";
  public static final String COURSE_BY_ID_URL = "/courses/{courseId}";
  public static final String COURSE_COEFFICIENT_STATUS_URL =
      "/courses/{courseId}/coefficient-status";

  public static final String CLASS_GROUPS_URL = "/class-groups";
  public static final String ACADEMIC_YEARS_URL = "/academic-years";
  public static final String GROUP_ASSIGNMENT_URL =
      "/students/{studentId}/academic-years/{academicYearId}/group-assignment";
  public static final String GROUP_STUDENTS_URL =
      "/academic-years/{academicYearId}/groups/{groupId}/students";
  public static final String TEACHER_ASSIGNMENT_URL =
      "/courses/{courseId}/academic-years/{academicYearId}/teachers";
  public static final String TEACHER_COURSES_URL =
      "/teachers/{teacherId}/academic-years/{academicYearId}/courses";

  public static final String EXAMS_URL =
      "/courses/{courseId}/academic-years/{academicYearId}/exams";
  public static final String EXAM_BY_ID_URL = "/exams/{examId}";
  public static final String GRADES_URL = "/grades";
  public static final String GRADE_BY_ID_URL = "/grades/{gradeId}";
  public static final String GRADE_HISTORY_URL = "/grades/{gradeId}/history";
  public static final String STUDENT_GRADES_URL = "/students/{studentId}/grades";

  public static final String TRANSCRIPT_SUMMARY_URL =
      "/students/{studentId}/academic-years/{academicYearId}/transcript-summary";
  public static final String TRANSCRIPT_REQUESTS_URL =
      "/students/{studentId}/academic-years/{academicYearId}/transcript-requests";
  public static final String PROMOTIONS_URL = "/promotions";
  public static final String PROMOTION_GRADUATES_URL = "/promotions/{promotionYear}/graduates";
  public static final String PROMOTION_GRADUATES_XLSX_URL =
      "/promotions/{promotionYear}/graduates.xlsx";
  public static final String DIPLOMA_STATUS_URL = "/students/{studentId}/diploma-status";

  private final TestRestTemplate testRestTemplate;

  public UserWithToken login(String email) {
    return testRestTemplate.postForObject(
        LOGIN_URL, new LoginPayload(email, SEEDED_PASSWORD), UserWithToken.class);
  }

  public String tokenOf(String email) {
    return login(email).token();
  }

  public static HttpHeaders authHeaders(String token) {
    var headers = jsonHeaders();
    headers.setBearerAuth(token);
    return headers;
  }

  public static HttpHeaders jsonHeaders() {
    var headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);
    return headers;
  }
}
