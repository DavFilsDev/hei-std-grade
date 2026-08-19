package school.hei.stdgrade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import school.hei.stdgrade.model.AcademicYear;
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
  private static final String ACADEMIC_YEAR_ID = "year-2024";
  private static final String EXAM_1_ID = "exam-1";
  private static final String EXAM_2_ID = "exam-2";
  private static final String RETAKE_EXAM_ID = "retake-exam-1";

  private User student;
  private Course course;
  private AcademicYear academicYear;
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

    academicYear = new AcademicYear(ACADEMIC_YEAR_ID, "2024-2025", 2024);

    exam1 =
        new Exam(EXAM_1_ID, Instant.now(), 0.5, SessionType.REGULAR, COURSE_ID, ACADEMIC_YEAR_ID);

    exam2 =
        new Exam(EXAM_2_ID, Instant.now(), 0.5, SessionType.REGULAR, COURSE_ID, ACADEMIC_YEAR_ID);

    retakeExam =
        new Exam(
            RETAKE_EXAM_ID, Instant.now(), null, SessionType.RETAKE, COURSE_ID, ACADEMIC_YEAR_ID);

    grade1 = new Grade("grade-1", STUDENT_ID, EXAM_1_ID, 14.0);

    grade2 = new Grade("grade-2", STUDENT_ID, EXAM_2_ID, 16.0);
  }

  @Test
  void computeRawCourseAverage_shouldReturnWeightedAverage() {
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(List.of(toJGrade(grade1), toJGrade(grade2)));

    when(jExamMapper.toDomain(any()))
        .thenAnswer(
            invocation -> {
              Object arg = invocation.getArgument(0);
              if (arg instanceof school.hei.stdgrade.repository.model.JExam) {
                school.hei.stdgrade.repository.model.JExam je =
                    (school.hei.stdgrade.repository.model.JExam) arg;
                return new Exam(
                    je.getId(),
                    je.getExamDate(),
                    je.getCoefficient(),
                    SessionType.valueOf(je.getSessionType()),
                    je.getCourseId(),
                    je.getAcademicYearId());
              }
              return null;
            });

    when(jGradeMapper.toDomain(any()))
        .thenAnswer(
            invocation -> {
              Object arg = invocation.getArgument(0);
              if (arg instanceof school.hei.stdgrade.repository.model.JGrade) {
                school.hei.stdgrade.repository.model.JGrade jg =
                    (school.hei.stdgrade.repository.model.JGrade) arg;
                return new Grade(jg.getId(), jg.getStudentId(), jg.getExamId(), jg.getScore());
              }
              return null;
            });

    Double result = service.computeRawCourseAverage(STUDENT_ID, COURSE_ID, ACADEMIC_YEAR_ID);

    assertEquals(15.0, result, 0.01);
  }

  @Test
  void computeRawCourseAverage_shouldReturnNullWhenMissingGrade() {
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(List.of(toJGrade(grade1)));

    when(jExamMapper.toDomain(any()))
        .thenAnswer(
            invocation -> {
              Object arg = invocation.getArgument(0);
              if (arg instanceof school.hei.stdgrade.repository.model.JExam) {
                school.hei.stdgrade.repository.model.JExam je =
                    (school.hei.stdgrade.repository.model.JExam) arg;
                return new Exam(
                    je.getId(),
                    je.getExamDate(),
                    je.getCoefficient(),
                    SessionType.valueOf(je.getSessionType()),
                    je.getCourseId(),
                    je.getAcademicYearId());
              }
              return null;
            });

    when(jGradeMapper.toDomain(any()))
        .thenAnswer(
            invocation -> {
              Object arg = invocation.getArgument(0);
              if (arg instanceof school.hei.stdgrade.repository.model.JGrade) {
                school.hei.stdgrade.repository.model.JGrade jg =
                    (school.hei.stdgrade.repository.model.JGrade) arg;
                return new Grade(jg.getId(), jg.getStudentId(), jg.getExamId(), jg.getScore());
              }
              return null;
            });

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
  void computeFinalCourseAverage_shouldReturnRawAverageWhenRetakeNotNeeded() {
    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(List.of(toJGrade(grade1), toJGrade(grade2)));

    when(jExamMapper.toDomain(any()))
        .thenAnswer(
            invocation -> {
              Object arg = invocation.getArgument(0);
              if (arg instanceof school.hei.stdgrade.repository.model.JExam) {
                school.hei.stdgrade.repository.model.JExam je =
                    (school.hei.stdgrade.repository.model.JExam) arg;
                return new Exam(
                    je.getId(),
                    je.getExamDate(),
                    je.getCoefficient(),
                    SessionType.valueOf(je.getSessionType()),
                    je.getCourseId(),
                    je.getAcademicYearId());
              }
              return null;
            });

    when(jGradeMapper.toDomain(any()))
        .thenAnswer(
            invocation -> {
              Object arg = invocation.getArgument(0);
              if (arg instanceof school.hei.stdgrade.repository.model.JGrade) {
                school.hei.stdgrade.repository.model.JGrade jg =
                    (school.hei.stdgrade.repository.model.JGrade) arg;
                return new Grade(jg.getId(), jg.getStudentId(), jg.getExamId(), jg.getScore());
              }
              return null;
            });

    Double result = service.computeFinalCourseAverage(STUDENT_ID, COURSE_ID, ACADEMIC_YEAR_ID);

    assertEquals(15.0, result, 0.01);
  }

  @Test
  void computeFinalCourseAverage_shouldApplyRetakeRuleWhenRetakeScoreIs10OrMore() {
    // Raw average is 8.0 (failing)
    Grade lowGrade1 = new Grade("grade-1", STUDENT_ID, EXAM_1_ID, 8.0);
    Grade lowGrade2 = new Grade("grade-2", STUDENT_ID, EXAM_2_ID, 8.0);
    Grade retakeGrade = new Grade("retake-grade", STUDENT_ID, RETAKE_EXAM_ID, 12.0);

    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2), toJExam(retakeExam)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(List.of(toJGrade(lowGrade1), toJGrade(lowGrade2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(RETAKE_EXAM_ID)))
        .thenReturn(List.of(toJGrade(retakeGrade)));

    when(jExamMapper.toDomain(any()))
        .thenAnswer(
            invocation -> {
              Object arg = invocation.getArgument(0);
              if (arg instanceof school.hei.stdgrade.repository.model.JExam) {
                school.hei.stdgrade.repository.model.JExam je =
                    (school.hei.stdgrade.repository.model.JExam) arg;
                return new Exam(
                    je.getId(),
                    je.getExamDate(),
                    je.getCoefficient(),
                    SessionType.valueOf(je.getSessionType()),
                    je.getCourseId(),
                    je.getAcademicYearId());
              }
              return null;
            });

    when(jGradeMapper.toDomain(any()))
        .thenAnswer(
            invocation -> {
              Object arg = invocation.getArgument(0);
              if (arg instanceof school.hei.stdgrade.repository.model.JGrade) {
                school.hei.stdgrade.repository.model.JGrade jg =
                    (school.hei.stdgrade.repository.model.JGrade) arg;
                return new Grade(jg.getId(), jg.getStudentId(), jg.getExamId(), jg.getScore());
              }
              return null;
            });

    Double result = service.computeFinalCourseAverage(STUDENT_ID, COURSE_ID, ACADEMIC_YEAR_ID);

    assertEquals(10.0, result, 0.01);
  }

  @Test
  void computeFinalCourseAverage_shouldKeepRawAverageWhenRetakeScoreIsBelow10() {
    Grade lowGrade1 = new Grade("grade-1", STUDENT_ID, EXAM_1_ID, 8.0);
    Grade lowGrade2 = new Grade("grade-2", STUDENT_ID, EXAM_2_ID, 8.0);
    Grade retakeGrade = new Grade("retake-grade", STUDENT_ID, RETAKE_EXAM_ID, 6.0);

    when(jExamRepository.findByCourseIdAndAcademicYearId(COURSE_ID, ACADEMIC_YEAR_ID))
        .thenReturn(List.of(toJExam(exam1), toJExam(exam2), toJExam(retakeExam)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(EXAM_1_ID, EXAM_2_ID)))
        .thenReturn(List.of(toJGrade(lowGrade1), toJGrade(lowGrade2)));
    when(jGradeRepository.findByStudentIdAndExamIdIn(STUDENT_ID, List.of(RETAKE_EXAM_ID)))
        .thenReturn(List.of(toJGrade(retakeGrade)));

    when(jExamMapper.toDomain(any()))
        .thenAnswer(
            invocation -> {
              Object arg = invocation.getArgument(0);
              if (arg instanceof school.hei.stdgrade.repository.model.JExam) {
                school.hei.stdgrade.repository.model.JExam je =
                    (school.hei.stdgrade.repository.model.JExam) arg;
                return new Exam(
                    je.getId(),
                    je.getExamDate(),
                    je.getCoefficient(),
                    SessionType.valueOf(je.getSessionType()),
                    je.getCourseId(),
                    je.getAcademicYearId());
              }
              return null;
            });

    when(jGradeMapper.toDomain(any()))
        .thenAnswer(
            invocation -> {
              Object arg = invocation.getArgument(0);
              if (arg instanceof school.hei.stdgrade.repository.model.JGrade) {
                school.hei.stdgrade.repository.model.JGrade jg =
                    (school.hei.stdgrade.repository.model.JGrade) arg;
                return new Grade(jg.getId(), jg.getStudentId(), jg.getExamId(), jg.getScore());
              }
              return null;
            });

    Double result = service.computeFinalCourseAverage(STUDENT_ID, COURSE_ID, ACADEMIC_YEAR_ID);

    assertEquals(8.0, result, 0.01);
  }

  @Test
  void computeYearDecision_shouldReturnPassWhenRawAverageIs10OrMore() {
    // Will be implemented with proper mocks
  }

  @Test
  void resolveLatestAttemptYear_shouldReturnMostRecentAcademicYear() {}

  @Test
  void isDiplomed_shouldReturnFalseWhenStudentHasNoTrack() {
    User noTrackStudent =
        new User(
            STUDENT_ID,
            "STD24190",
            "Student",
            "Test",
            "student@test.com",
            "hash",
            true,
            LocalDate.of(2024, 9, 1),
            null,
            List.of(RoleName.ROLE_STUDENT));

    when(jUserRepository.findById(STUDENT_ID)).thenReturn(Optional.of(toJUser(noTrackStudent)));

    when(jUserMapper.toDomain(any())).thenReturn(noTrackStudent);

    boolean result = service.isDiplomed(STUDENT_ID);

    assertFalse(result);
  }

  private school.hei.stdgrade.repository.model.JExam toJExam(Exam exam) {
    school.hei.stdgrade.repository.model.JExam je =
        new school.hei.stdgrade.repository.model.JExam();
    je.setId(exam.id());
    je.setExamDate(exam.examDate());
    je.setCoefficient(exam.coefficient());
    je.setSessionType(exam.sessionType().name());
    je.setCourseId(exam.courseId());
    je.setAcademicYearId(exam.academicYearId());
    return je;
  }

  private school.hei.stdgrade.repository.model.JGrade toJGrade(Grade grade) {
    school.hei.stdgrade.repository.model.JGrade jg =
        new school.hei.stdgrade.repository.model.JGrade();
    jg.setId(grade.id());
    jg.setStudentId(grade.studentId());
    jg.setExamId(grade.examId());
    jg.setScore(grade.score());
    return jg;
  }

  private school.hei.stdgrade.repository.model.JUser toJUser(User user) {
    school.hei.stdgrade.repository.model.JUser ju =
        new school.hei.stdgrade.repository.model.JUser();
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
