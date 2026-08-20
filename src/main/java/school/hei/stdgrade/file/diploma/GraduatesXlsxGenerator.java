package school.hei.stdgrade.file.diploma;

import java.io.ByteArrayOutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import school.hei.stdgrade.model.GraduateEntry;

@Component
public class GraduatesXlsxGenerator {
  public static final MediaType XLSX_MEDIA_TYPE =
      MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

  private static final String[] HEADERS = {"Rang", "STD", "Nom", "Prénom", "Moyenne générale"};

  public byte[] generate(List<GraduateEntry> graduates) {
    try (var workbook = new XSSFWorkbook();
        var out = new ByteArrayOutputStream()) {
      XSSFSheet sheet = workbook.createSheet("Diplômés");

      Row header = sheet.createRow(0);
      for (int col = 0; col < HEADERS.length; col++) {
        header.createCell(col).setCellValue(HEADERS[col]);
      }

      int rowIndex = 1;
      for (var graduate : graduates) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(graduate.rank());
        row.createCell(1).setCellValue(graduate.std());
        row.createCell(2).setCellValue(graduate.lastName());
        row.createCell(3).setCellValue(graduate.firstName());
        row.createCell(4).setCellValue(graduate.average());
      }

      for (int col = 0; col < HEADERS.length; col++) {
        sheet.autoSizeColumn(col);
      }

      workbook.write(out);
      return out.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate graduates XLSX", e);
    }
  }
}
