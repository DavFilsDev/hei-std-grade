package school.hei.stdgrade.service.event;

import static java.io.File.createTempFile;
import static java.nio.file.Files.write;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.stdgrade.endpoint.event.model.TranscriptRequestedEvent;
import school.hei.stdgrade.file.bucket.BucketComponent;
import school.hei.stdgrade.file.transcript.TranscriptPdfGenerator;
import school.hei.stdgrade.mail.Email;
import school.hei.stdgrade.mail.Mailer;
import school.hei.stdgrade.repository.JUserRepository;
import school.hei.stdgrade.repository.mapper.JUserMapper;
import school.hei.stdgrade.service.GradeCalculationService;

@Service
@AllArgsConstructor
@Slf4j
public class TranscriptRequestedEventService implements Consumer<TranscriptRequestedEvent> {
  private final JUserRepository jUserRepository;
  private final JUserMapper jUserMapper;
  private final GradeCalculationService gradeCalculationService;
  private final TranscriptPdfGenerator pdfGenerator;
  private final BucketComponent bucketComponent;
  private final Mailer mailer;

  @Override
  @SneakyThrows
  public void accept(TranscriptRequestedEvent event) {
    log.info(
        "Generating transcript for student={} year={}",
        event.getStudentId(),
        event.getAcademicYearId());

    var student =
        jUserRepository
            .findById(event.getStudentId())
            .map(jUserMapper::toDomain)
            .orElseThrow(
                () ->
                    new NoSuchElementException("User(id=" + event.getStudentId() + ") not found"));

    var summary =
        gradeCalculationService.computeTranscript(event.getStudentId(), event.getAcademicYearId());
    var generated = pdfGenerator.generate(student, summary);

    var tmpFile = toTempFile(generated.content(), generated.filename());
    var bucketKey = "transcripts/" + student.id() + "/" + event.getAcademicYearId() + ".pdf";
    bucketComponent.upload(tmpFile, bucketKey);

    mailer.accept(toEmail(student, generated, tmpFile));
    log.info("Transcript sent to {} (bucketKey={})", student.email(), bucketKey);
  }

  private File toTempFile(byte[] content, String filename) throws Exception {
    var tmpFile = createTempFile("transcript-", "-" + filename);
    write(tmpFile.toPath(), content);
    return tmpFile;
  }

  private Email toEmail(
      school.hei.stdgrade.model.User student,
      school.hei.stdgrade.file.transcript.GeneratedFile generated,
      File attachment)
      throws AddressException {
    return new Email(
        new InternetAddress(student.email()),
        List.of(),
        List.of(),
        "Votre relevé de notes - " + generated.filename(),
        "<p>Bonjour "
            + student.firstName()
            + ",</p><p>Veuillez trouver ci-joint votre relevé de notes.</p>",
        List.of(attachment));
  }
}
