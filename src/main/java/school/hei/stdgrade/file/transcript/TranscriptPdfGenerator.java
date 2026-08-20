package school.hei.stdgrade.file.transcript;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.TranscriptSummary;
import school.hei.stdgrade.model.User;

@Component
public class TranscriptPdfGenerator {
  private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
  private static final Font LABEL_FONT = new Font(Font.HELVETICA, 12, Font.BOLD);
  private static final Font BODY_FONT = new Font(Font.HELVETICA, 12);

  public GeneratedFile generate(User student, TranscriptSummary summary) {
    var out = new ByteArrayOutputStream();
    var document = new Document();
    try {
      PdfWriter.getInstance(document, out);
      document.open();

      var title = new Paragraph("HEI - Relevé de notes", TITLE_FONT);
      title.setAlignment(Element.ALIGN_CENTER);
      document.add(title);
      document.add(new Paragraph(" "));

      document.add(labeled("Étudiant : ", student.lastName() + " " + student.firstName()));
      document.add(labeled("STD : ", student.ref()));
      document.add(labeled("Année scolaire : ", summary.academicYearId()));
      document.add(labeled("Statut : ", summary.status().name()));
      document.add(new Paragraph(" "));
      document.add(labeled("Moyenne générale : ", summary.average() + " / 20"));
      document.add(labeled("Crédits obtenus : ", String.valueOf(summary.creditsEarned())));

      document.close();
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate transcript PDF", e);
    }

    var filename = "releve-" + student.ref() + "-" + summary.academicYearId() + ".pdf";
    return new GeneratedFile(out.toByteArray(), filename, MediaType.APPLICATION_PDF);
  }

  private Paragraph labeled(String label, String value) {
    var p = new Paragraph();
    p.add(new com.lowagie.text.Chunk(label, LABEL_FONT));
    p.add(new com.lowagie.text.Chunk(value, BODY_FONT));
    return p;
  }
}
