package pe.gob.sunass.vma.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.sunass.vma.model.RegistroVMA;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class GenerarExcelService {

    @Autowired
    private RegistroVMAService registroVMAService;

    public ByteArrayInputStream generarExcelActivos(Integer empresaId, String estado, Date fechaDesde, Date fechaHasta, String anio, String username) {
        List<RegistroVMA> registros = registroVMAService.searchRegistroVMA(empresaId, estado, fechaDesde, fechaHasta, anio, username);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Lista de registros VMA");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"N°", "Empresa EPS", "Tamaño de la EPS", "Régimen", "Estado", "Fecha de registro", "Año"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);

            CellStyle centeredStyle = workbook.createCellStyle();
            centeredStyle.setAlignment(HorizontalAlignment.CENTER);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            int rowIdx = 1;
            for (RegistroVMA registro : registros) {
                Row row = sheet.createRow(rowIdx++);


                Cell cell0 = row.createCell(0);
                cell0.setCellValue(rowIdx - 1);
                cell0.setCellStyle(centeredStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(registro.getEmpresa().getNombre());
                cell1.setCellStyle(centeredStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(registro.getEmpresa().getTipo());
                cell2.setCellStyle(centeredStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(registro.getEmpresa().getRegimen());
                cell3.setCellStyle(centeredStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(registro.getEstado());
                cell4.setCellStyle(centeredStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(formatter.format(registro.getCreatedAt()));
                cell5.setCellStyle(centeredStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(registro.getFichaRegistro().getAnio());
                cell6.setCellStyle(centeredStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el excel" + e.getMessage());
        }
    }
}