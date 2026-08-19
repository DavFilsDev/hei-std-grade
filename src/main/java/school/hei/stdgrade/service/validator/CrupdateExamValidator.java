package school.hei.stdgrade.service.validator;

import java.util.StringJoiner;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.Exam;
import school.hei.stdgrade.model.SessionType;
import school.hei.stdgrade.repository.JAcademicYearRepository;
import school.hei.stdgrade.repository.JCourseRepository;

@Component
@AllArgsConstructor
public class CrupdateExamValidator implements Consumer<Exam> {
  private final JCourseRepository jCourseRepository;
  private final JAcademicYearRepository jAcademicYearRepository;

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  @Override
  public void accept(Exam exam) {
    var sj = new StringJoiner(". ");

    if (isBlank(exam.id())) {
      sj.add("Id is mandatory");
    }
    if (exam.examDate() == null) {
      sj.add("ExamDate is mandatory");
    }
    if (exam.sessionType() == null) {
      sj.add("SessionType is mandatory");
    }
    if (exam.sessionType() == SessionType.REGULAR && exam.coefficient() == null) {
      sj.add("Coefficient is mandatory for REGULAR exam");
    }
    if (exam.sessionType() == SessionType.RETAKE && exam.coefficient() != null) {
      sj.add("Coefficient must be null for RETAKE exam");
    }

    if (isBlank(exam.courseId())) {
      sj.add("CourseId is mandatory");
    } else if (!jCourseRepository.existsById(exam.courseId())) {
      sj.add("Course(id=" + exam.courseId() + ") does not exist");
    }

    if (isBlank(exam.academicYearId())) {
      sj.add("AcademicYearId is mandatory");
    } else if (!jAcademicYearRepository.existsById(exam.academicYearId())) {
      sj.add("AcademicYear(id=" + exam.academicYearId() + ") does not exist");
    }

    if (sj.length() > 0) {
      throw new IllegalArgumentException(sj.toString());
    }
  }
}
