package school.hei.stdgrade.service;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.stdgrade.model.AcademicYear;
import school.hei.stdgrade.model.Course;
import school.hei.stdgrade.model.Exam;
import school.hei.stdgrade.model.Grade;
import school.hei.stdgrade.model.SessionType;
import school.hei.stdgrade.model.TranscriptStatus;
import school.hei.stdgrade.model.TranscriptSummary;
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

@Service
@AllArgsConstructor
public class GradeCalculationService {

  private final JGradeRepository jGradeRepository;
  private final JExamRepository jExamRepository;
  private final JCourseRepository jCourseRepository;
  private final JUserRepository jUserRepository;
  private final JAcademicYearRepository jAcademicYearRepository;

  private final JGradeMapper jGradeMapper;
  private final JExamMapper jExamMapper;
  private final JCourseMapper jCourseMapper;
  private final JUserMapper jUserMapper;
  private final JAcademicYearMapper jAcademicYearMapper;

  private final ExamService examService;

  public Double computeRawCourseAverage(String studentId, String courseId, String academicYearId) {
    List<Exam> regularExams =
        jExamRepository.findByCourseIdAndAcademicYearId(courseId, academicYearId).stream()
            .map(jExamMapper::toDomain)
            .filter(exam -> exam.sessionType() == SessionType.REGULAR)
            .toList();

    if (regularExams.isEmpty()) {
      return null;
    }

    List<String> examIds = regularExams.stream().map(Exam::id).toList();
    Map<String, Grade> gradeByExamId =
        jGradeRepository.findByStudentIdAndExamIdIn(studentId, examIds).stream()
            .map(jGradeMapper::toDomain)
            .collect(groupingBy(Grade::examId))
            .entrySet()
            .stream()
            .collect(
                java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

    for (Exam exam : regularExams) {
      if (!gradeByExamId.containsKey(exam.id())) {
        return null;
      }
    }

    double weightedSum = 0.0;
    double coefficientSum = 0.0;

    for (Exam exam : regularExams) {
      Grade grade = gradeByExamId.get(exam.id());
      double coeff = exam.coefficient() != null ? exam.coefficient() : 0.0;
      weightedSum += grade.score() * coeff;
      coefficientSum += coeff;
    }

    if (coefficientSum == 0.0) {
      return null;
    }

    return round(weightedSum / coefficientSum);
  }

  public Double computeFinalCourseAverage(
      String studentId, String courseId, String academicYearId) {
    Double rawAverage = computeRawCourseAverage(studentId, courseId, academicYearId);

    if (rawAverage == null) {
      return null;
    }
    if (rawAverage >= 10.0) {
      return rawAverage;
    }

    if (computeYearDecision(studentId, academicYearId) != YearDecision.PASS) {
      return rawAverage;
    }

    List<Exam> retakeExams =
        jExamRepository.findByCourseIdAndAcademicYearId(courseId, academicYearId).stream()
            .map(jExamMapper::toDomain)
            .filter(exam -> exam.sessionType() == SessionType.RETAKE)
            .toList();

    if (retakeExams.isEmpty()) {
      return rawAverage;
    }

    List<String> retakeExamIds = retakeExams.stream().map(Exam::id).toList();
    List<Grade> retakeGrades =
        jGradeRepository.findByStudentIdAndExamIdIn(studentId, retakeExamIds).stream()
            .map(jGradeMapper::toDomain)
            .toList();

    if (retakeGrades.isEmpty()) {
      return rawAverage;
    }

    double retakeScore = retakeGrades.get(0).score();
    return retakeScore >= 10.0 ? 10.0 : rawAverage;
  }

  public YearDecision computeYearDecision(String studentId, String academicYearId) {
    Double rawYearAverage = computeRawYearAverage(studentId, academicYearId);

    if (rawYearAverage == null) {
      return YearDecision.INCOMPLETE;
    }
    return rawYearAverage < 10.0 ? YearDecision.REPEAT : YearDecision.PASS;
  }

  public Double computeRawYearAverage(String studentId, String academicYearId) {
    User student = getUser(studentId);
    List<Course> courses = getCoursesOfferedInYear(student, academicYearId);

    double weightedSum = 0.0;
    int totalCredits = 0;

    for (Course course : courses) {
      Double courseAvg = computeRawCourseAverage(studentId, course.id(), academicYearId);
      if (courseAvg == null) {
        return null;
      }
      weightedSum += courseAvg * course.credits();
      totalCredits += course.credits();
    }

    if (totalCredits == 0) {
      return null;
    }
    return round(weightedSum / totalCredits);
  }

  public Optional<AcademicYear> resolveLatestAttemptYear(String studentId, String courseId) {
    List<Exam> allExamsForCourse =
        jExamRepository.findByCourseId(courseId).stream().map(jExamMapper::toDomain).toList();

    if (allExamsForCourse.isEmpty()) {
      return Optional.empty();
    }

    List<String> examIds = allExamsForCourse.stream().map(Exam::id).toList();
    List<Grade> grades =
        jGradeRepository.findByStudentIdAndExamIdIn(studentId, examIds).stream()
            .map(jGradeMapper::toDomain)
            .toList();

    if (grades.isEmpty()) {
      return Optional.empty();
    }

    List<String> academicYearIds =
        allExamsForCourse.stream()
            .filter(exam -> grades.stream().anyMatch(g -> g.examId().equals(exam.id())))
            .map(Exam::academicYearId)
            .distinct()
            .toList();

    return jAcademicYearRepository.findAllById(academicYearIds).stream()
        .map(jAcademicYearMapper::toDomain)
        .max(comparing(AcademicYear::startYear));
  }

  public TranscriptSummary computeTranscript(String studentId, String academicYearId) {
    User student = getUser(studentId);
    List<Course> courses = getCoursesOfferedInYear(student, academicYearId);

    boolean allCoursesFinalized =
        courses.stream()
            .allMatch(course -> examService.isCourseFinalized(course.id(), academicYearId));

    boolean allGradesEntered = true;
    double weightedSum = 0.0;
    int totalCredits = 0;
    int creditsEarned = 0;

    for (Course course : courses) {
      Double finalAvg = computeFinalCourseAverage(studentId, course.id(), academicYearId);
      if (finalAvg == null) {
        allGradesEntered = false;
      } else {
        weightedSum += finalAvg * course.credits();
        if (finalAvg >= 10.0) {
          creditsEarned += course.credits();
        }
      }
      totalCredits += course.credits();
    }

    var status =
        (allCoursesFinalized && allGradesEntered)
            ? TranscriptStatus.DEFINITIVE
            : TranscriptStatus.PROVISIONAL;

    double average = totalCredits > 0 ? round(weightedSum / totalCredits) : 0.0;

    return new TranscriptSummary(studentId, academicYearId, status, average, creditsEarned);
  }

  public boolean isDiplomed(String studentId) {
    User student = getUser(studentId);

    if (student.trackId() == null) {
      return false;
    }

    for (Course course : getAllCoursesForTrack(student.trackId())) {
      Optional<AcademicYear> latestYearOpt = resolveLatestAttemptYear(studentId, course.id());

      if (latestYearOpt.isEmpty()) {
        return false;
      }

      AcademicYear latestYear = latestYearOpt.get();
      if (!examService.isCourseFinalized(course.id(), latestYear.id())) {
        continue;
      }

      Double finalAvg = computeFinalCourseAverage(studentId, course.id(), latestYear.id());
      if (finalAvg == null || finalAvg < 10.0) {
        return false;
      }
    }
    return true;
  }

  public double computeThreeYearAverage(String studentId) {
    User student = getUser(studentId);
    if (student.trackId() == null) {
      return 0.0;
    }

    double weightedSum = 0.0;
    int totalCredits = 0;

    for (Course course : getAllCoursesForTrack(student.trackId())) {
      Optional<AcademicYear> latestYearOpt = resolveLatestAttemptYear(studentId, course.id());
      if (latestYearOpt.isEmpty()) {
        continue;
      }
      Double finalAvg = computeFinalCourseAverage(studentId, course.id(), latestYearOpt.get().id());
      if (finalAvg == null) {
        continue;
      }
      weightedSum += finalAvg * course.credits();
      totalCredits += course.credits();
    }

    return totalCredits > 0 ? round(weightedSum / totalCredits) : 0.0;
  }

  private User getUser(String studentId) {
    return jUserRepository
        .findById(studentId)
        .map(jUserMapper::toDomain)
        .orElseThrow(() -> new NoSuchElementException("User(id=" + studentId + ") not found"));
  }

  private List<Course> getCoursesOfferedInYear(User student, String academicYearId) {
    var courseIdsWithExamsThisYear =
        jExamRepository.findByAcademicYearId(academicYearId).stream()
            .map(jExamMapper::toDomain)
            .map(Exam::courseId)
            .distinct()
            .toList();

    return jCourseRepository.findAllById(courseIdsWithExamsThisYear).stream()
        .map(jCourseMapper::toDomain)
        .filter(course -> course.trackId() == null || course.trackId().equals(student.trackId()))
        .toList();
  }

  private List<Course> getAllCoursesForTrack(String trackId) {
    return jCourseRepository.findAll().stream()
        .map(jCourseMapper::toDomain)
        .filter(course -> course.trackId() == null || course.trackId().equals(trackId))
        .toList();
  }

  private static double round(double value) {
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
  }

  public enum YearDecision {
    PASS,
    REPEAT,
    INCOMPLETE
  }
}
