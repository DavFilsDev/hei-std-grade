package school.hei.stdgrade.service.validator;

import java.util.StringJoiner;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.Grade;
import school.hei.stdgrade.repository.JExamRepository;
import school.hei.stdgrade.repository.JUserRepository;

@Component
@AllArgsConstructor
public class CrupdateGradeValidator implements Consumer<Grade> {
  private final JExamRepository jExamRepository;
  private final JUserRepository jUserRepository;

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  @Override
  public void accept(Grade grade) {
    var sj = new StringJoiner(". ");

    if (isBlank(grade.id())) {
      sj.add("Id is mandatory");
    }
    if (isBlank(grade.studentId())) {
      sj.add("StudentId is mandatory");
    } else if (!jUserRepository.existsById(grade.studentId())) {
      sj.add("User(id=" + grade.studentId() + ") does not exist");
    }
    if (isBlank(grade.examId())) {
      sj.add("ExamId is mandatory");
    } else if (!jExamRepository.existsById(grade.examId())) {
      sj.add("Exam(id=" + grade.examId() + ") does not exist");
    }
    if (grade.score() < 0 || grade.score() > 20) {
      sj.add("Score must be between 0 and 20");
    }

    if (sj.length() > 0) {
      throw new IllegalArgumentException(sj.toString());
    }
  }
}
