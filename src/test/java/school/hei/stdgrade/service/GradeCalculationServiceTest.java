package school.hei.stdgrade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static school.hei.stdgrade.service.GradeCalculationService.YearDecision.INCOMPLETE;
import static school.hei.stdgrade.service.GradeCalculationService.YearDecision.PASS;
import static school.hei.stdgrade.service.GradeCalculationService.YearDecision.REPEAT;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.hei.stdgrade.model.Course;
import school.hei.stdgrade.model.Exam;
import school.hei.stdgrade.model.Grade;
import school.hei.stdgrade.model.RoleName;
import school.hei.stdgrade.model.SessionType;
import school.hei.stdgrade.model.User;
import school.hei.stdgrade.repository.JAcademicYearRepository;
import school.hei.stdgrade.repository.JCourseRepository;
import school.hei.stdgrade.repository.JExamRepository;
import school.hei.stdgrade.repository.JGradeRepository;
import school.hei.stdgrade.repository.JUserRepository;
import school.hei.stdgrade.repository.mapper.JAcademicYearMapper;
import school.hei.stdgrade.repository.mapper.JCourseMapper;
import school.hei.stdgrade.repository.mapper.JExamMapper;
import school.hei.stdgrade.repository.mapper.JGradeMapper;
import school.hei.stdgrade.repository.mapper.JUserMapper;
import school.hei.stdgrade.repository.model.JCourse;
import school.hei.stdgrade.repository.model.JExam;
import school.hei.stdgrade.repository.model.JGrade;
import school.hei.stdgrade.repository.model.JUser;

@ExtendWith(MockitoExtension.class)
class GradeCalculationServiceTest {

  @Mock private JGradeRepository jGradeRepository;
  @Mock private JExamRepository jExamRepository;
  @Mock private JCourseRepository jCourseRepository;
  @Mock private JUserRepository jUserRepository;
  @Mock private JAcademicYearRepository jAcademicYearRepository;
  @Mock private ExamService examService;
  @Mock private JGradeMapper jGradeMapper;
  @Mock private JExamMapper jExamMapper;
  @Mock private JCourseMapper jCourseMapper;
  @Mock private JUserMapper jUserMapper;
  @Mock private JAcademicYearMapper jAcademicYearMapper;

  @InjectMocks private GradeCalculationService service;

  private static final String STUDENT_ID = "student-1";
  private static final String COURSE_ID = "course-1";
  private static final String COURSE_2_ID = "course-2";
  private static final String ACADEMIC_YEAR_ID = "year-2024";
  private static final String EXAM_1_ID = "exam-1";
  private static final String EXAM_2_ID = "exam-2";
  private static final String RETAKE_EXAM_ID = "retake-exam-1";
  private static final String COURSE_2_EXAM_ID = "exam-course2";

  private User student;
  private Course course;
  private Course course2;
  private Exam exam1;
  private Exam exam2;
  private Exam retakeExam;
  private Grade grade1;
  private Grade grade2;

  @BeforeEach
  void setUp() {
    student =
        new User(
            STUDENT_ID,
            "STD24190",
            "Student",
            "Test",
            "student@test.com",
            "hash",
            true,
            LocalDate.of(2024, 9, 1),
            "track-tn",
            List.of(RoleName.ROLE_STUDENT));

    course = new Course(COURSE_ID, "donnees1", "Données 1", 5, "teaching-unit-1", null);
    course2 = new Course(COURSE_2_ID, "algo1", "Algo 1", 5, "teaching-unit-2", null);

    exam1 =
        new Exam(EXAM_1_ID, Instant.now(), 0.5, SessionType.REGULAR, COURSE_ID, ACADEMIC_YEAR_ID);
    exam2 =
        new Exam(EXAM_2_ID, Instant.now(), 0.5, SessionType.REGULAR, COURSE_ID, ACADEMIC_YEAR_ID);
    retakeExam =
        new Exam(
            RETAKE_EXAM_ID, Instant.now(), null, SessionType.RETAKE, COURSE_ID, ACADEMIC_YEAR_ID);

    grade1 = new Grade("grade-1", STUDENT_ID, EXAM_1_ID, 14.0);
    grade2 = new Grade("grade-2", STUDENT_ID, EXAM_2_ID, 16.0);

    lenient()
        .when(jExamMapper.toDomain(any(JExam.class)))
        .thenAnswer(
            inv -> {
              JExam je = inv.getArgument(0);
              return new Exam(
                  je.getId(),
                  je.getExamDate(),
                  je.getCoefficient(),
                  SessionType.valueOf(je.getSessionType()),
                  je.getCourseId(),
                  je.getAcademicYearId());
            });
    lenient()
        .when(jGradeMapper.toDomain(any(JGrade.class)))
        .thenAnswer(
            inv -> {
              JGrade jg = inv.getArgument(0);
              return new Grade(jg.getId(), jg.getStudentId(), jg.getExamId(), jg.getScore());
            });
    lenient()
        .when(jCourseMapper.toDomain(any(JCourse.class)))
        .thenAnswer(
            inv -> {
              JCourse jc = inv.getArgument(0);
              return new Course(
                  jc.getId(),
                  jc.getRef(),
                  jc.getTitle(),
                  jc.getCredits(),
                  jc.getTeachingUnitId(),
                  jc.getTrackId());
            });
  }

  @Test
  void computeRawCourseAverage_shouldReturnWeightedAverage() {
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(List.of(toJGrade(grade1), toJGrade(grade2)));

    Double result = service.computeRawCourseAverage(STUDENT_ID, COURSE_ID, ACADEMIC_YEAR_ID);

    assertEquals(15.0, result, 0.01);
  }

  @Test
  void computeRawCourseAverage_shouldReturnNullWhenMissingGrade() {
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(List.of(toJGrade(grade1)));

    Double result = service.computeRawCourseAverage(STUDENT_ID, COURSE_ID, ACADEMIC_YEAR_ID);

    assertNull(result);
  }

  @Test
  void computeRawCourseAverage_shouldReturnNullWhenNoRegularExams() {
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of());

    Double result = service.computeRawCourseAverage(STUDENT_ID, COURSE_ID, ACADEMIC_YEAR_ID);

    assertNull(result);
  }

  @Test
  void computeFinalCourseAverage_shouldApplyRetakeRuleWhenYearIsPassedAndRetakeScoreIs10OrMore() {
    Grade lowGrade1 = new Grade("g1", STUDENT_ID, EXAM_1_ID, 8.0);
    Grade lowGrade2 = new Grade("g2", STUDENT_ID, EXAM_2_ID, 8.0);
    Grade retakeGrade = new Grade("retake-grade", STUDENT_ID, RETAKE_EXAM_ID, 12.0);

    stubYearWithTwoCourses(course2RawAverage(16.0));
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2), toJExam(retakeExam)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(List.of(toJGrade(lowGrade1), toJGrade(lowGrade2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(RETAKE_EXAM_ID)))
        .thenReturn(List.of(toJGrade(retakeGrade)));

    Double result = service.computeFinalCourseAverage(STUDENT_ID, COURSE_ID, ACADEMIC_YEAR_ID);

    assertEquals(10.0, result, 0.01);
  }

  @Test
  void computeFinalCourseAverage_shouldKeepRawAverageWhenYearIsPassedButRetakeScoreBelow10() {
    Grade lowGrade1 = new Grade("g1", STUDENT_ID, EXAM_1_ID, 8.0);
    Grade lowGrade2 = new Grade("g2", STUDENT_ID, EXAM_2_ID, 8.0);
    Grade retakeGrade = new Grade("retake-grade", STUDENT_ID, RETAKE_EXAM_ID, 6.0);

    stubYearWithTwoCourses(course2RawAverage(16.0));
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2), toJExam(retakeExam)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(List.of(toJGrade(lowGrade1), toJGrade(lowGrade2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(RETAKE_EXAM_ID)))
        .thenReturn(List.of(toJGrade(retakeGrade)));

    Double result = service.computeFinalCourseAverage(STUDENT_ID, COURSE_ID, ACADEMIC_YEAR_ID);

    assertEquals(8.0, result, 0.01);
  }

  @Test
  void computeFinalCourseAverage_shouldIgnoreRetake_whenYearIsRepeated() {
    Grade lowGrade1 = new Grade("g1", STUDENT_ID, EXAM_1_ID, 8.0);
    Grade lowGrade2 = new Grade("g2", STUDENT_ID, EXAM_2_ID, 8.0);
    Grade retakeGrade = new Grade("retake-grade", STUDENT_ID, RETAKE_EXAM_ID, 12.0);

    stubYearWithTwoCourses(course2RawAverage(6.0));
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2), toJExam(retakeExam)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(List.of(toJGrade(lowGrade1), toJGrade(lowGrade2)));

    Double result = service.computeFinalCourseAverage(STUDENT_ID, COURSE_ID, ACADEMIC_YEAR_ID);

    assertEquals(8.0, result, 0.01);
  }

  @Test
  void computeFinalCourseAverage_shouldReturnRawAverageWhenNoRetakeNeeded() {
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(List.of(toJGrade(grade1), toJGrade(grade2)));

    Double result = service.computeFinalCourseAverage(STUDENT_ID, COURSE_ID, ACADEMIC_YEAR_ID);

    assertEquals(15.0, result, 0.01);
  }

  @Test
  void computeYearDecision_shouldReturnPass_whenWeightedAverageIsAtLeast10() {
    stubYearWithTwoCourses(course2RawAverage(16.0));
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(
            List.of(
                toJGrade(new Grade("g1", STUDENT_ID, EXAM_1_ID, 8.0)),
                toJGrade(new Grade("g2", STUDENT_ID, EXAM_2_ID, 8.0))));

    assertEquals(PASS, service.computeYearDecision(STUDENT_ID, ACADEMIC_YEAR_ID));
  }

  @Test
  void computeYearDecision_shouldReturnRepeat_whenWeightedAverageIsBelow10() {
    stubYearWithTwoCourses(course2RawAverage(6.0));
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(
            List.of(
                toJGrade(new Grade("g1", STUDENT_ID, EXAM_1_ID, 8.0)),
                toJGrade(new Grade("g2", STUDENT_ID, EXAM_2_ID, 8.0))));

    assertEquals(REPEAT, service.computeYearDecision(STUDENT_ID, ACADEMIC_YEAR_ID));
  }

  @Test
  void computeYearDecision_shouldReturnIncomplete_whenAGradeIsMissing() {
    when(jUserRepository.findById(STUDENT_ID)).thenReturn(Optional.of(toJUser(student)));
    when(jUserMapper.toDomain(any(JUser.class))).thenReturn(student);
    when(jExamRepository.findByAcademicYearId(ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2)));
    when(jCourseRepository.findAllById(List.of(COURSE_ID))).thenReturn(List.of(toJCourse(course)));
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(List.of(toJGrade(grade1)));

    assertEquals(INCOMPLETE, service.computeYearDecision(STUDENT_ID, ACADEMIC_YEAR_ID));
  }

  @Test
  void computeRawYearAverage_shouldOnlyCountCoursesOfferedInThatAcademicYear() {
    when(jUserRepository.findById(STUDENT_ID)).thenReturn(Optional.of(toJUser(student)));
    when(jUserMapper.toDomain(any(JUser.class))).thenReturn(student);
    when(jExamRepository.findByAcademicYearId(ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2)));
    when(jCourseRepository.findAllById(List.of(COURSE_ID))).thenReturn(List.of(toJCourse(course)));
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(List.of(toJGrade(grade1), toJGrade(grade2)));

    Double result = service.computeRawYearAverage(STUDENT_ID, ACADEMIC_YEAR_ID);

    assertEquals(15.0, result, 0.01);
  }

  @Test
  void isDiplomed_shouldReturnFalseWhenStudentHasNoTrack() {
    User noTrackStudent = student.toBuilder().trackId(null).build();

    when(jUserRepository.findById(STUDENT_ID)).thenReturn(Optional.of(toJUser(noTrackStudent)));
    when(jUserMapper.toDomain(any(JUser.class))).thenReturn(noTrackStudent);

    assertFalse(service.isDiplomed(STUDENT_ID));
  }

  private void stubYearWithTwoCourses(double course2RawScore) {
    Exam course2Exam =
        new Exam(
            COURSE_2_EXAM_ID,
            Instant.now(),
            1.0,
            SessionType.REGULAR,
            COURSE_2_ID,
            ACADEMIC_YEAR_ID);
    Grade course2Grade = new Grade("g-course2", STUDENT_ID, COURSE_2_EXAM_ID, course2RawScore);

    when(jUserRepository.findById(STUDENT_ID)).thenReturn(Optional.of(toJUser(student)));
    when(jUserMapper.toDomain(any(JUser.class))).thenReturn(student);
    when(jExamRepository.findByAcademicYearId(ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2), toJExam(course2Exam)));
    when(jCourseRepository.findAllById(List.of(COURSE_ID, COURSE_2_ID)))
        .thenReturn(List.of(toJCourse(course), toJCourse(course2)));

    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_2_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(course2Exam)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(COURSE_2_EXAM_ID)))
        .thenReturn(List.of(toJGrade(course2Grade)));
  }

  private double course2RawAverage(double value) {
    return value;
  }

  private JExam toJExam(Exam exam) {
    JExam je = new JExam();
    je.setId(exam.id());
    je.setExamDate(exam.examDate());
    je.setCoefficient(exam.coefficient());
    je.setSessionType(exam.sessionType().name());
    je.setCourseId(exam.courseId());
    je.setAcademicYearId(exam.academicYearId());
    return je;
  }

  private JGrade toJGrade(Grade grade) {
    JGrade jg = new JGrade();
    jg.setId(grade.id());
    jg.setStudentId(grade.studentId());
    jg.setExamId(grade.examId());
    jg.setScore(grade.score());
    return jg;
  }

  private JCourse toJCourse(Course c) {
    JCourse jc = new JCourse();
    jc.setId(c.id());
    jc.setRef(c.ref());
    jc.setTitle(c.title());
    jc.setCredits(c.credits());
    jc.setTeachingUnitId(c.teachingUnitId());
    jc.setTrackId(c.trackId());
    return jc;
  }

  private JUser toJUser(User user) {
    JUser ju = new JUser();
    ju.setId(user.id());
    ju.setRef(user.ref());
    ju.setLastName(user.lastName());
    ju.setFirstName(user.firstName());
    ju.setEmail(user.email());
    ju.setPasswordHash(user.passwordHash());
    ju.setEnabled(user.isEnabled());
    ju.setEntranceDate(user.entranceDate());
    ju.setTrackId(user.trackId());
    return ju;
  }
}
