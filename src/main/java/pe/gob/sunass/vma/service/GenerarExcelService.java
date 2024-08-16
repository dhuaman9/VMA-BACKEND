package pe.gob.sunass.vma.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.sunass.vma.dto.*;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;
import pe.gob.sunass.vma.repository.RegistroVMARepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenerarExcelService {

    @Autowired
    private RegistroVMARepository registroVMARepository;

    @Autowired
    private CuestionarioService cuestionarioService;

    public ByteArrayInputStream generarExcelCuestionario(List<Integer> registrosIds) {
        List<RegistroVMA> registros = registroVMARepository.findRegistrosVmasPorIds(registrosIds);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Lista de registros VMA");

            Row headerRow = sheet.createRow(0);
            List<String> headersList = new ArrayList<>(Arrays.asList("N°", "Empresa EPS", "Tamaño de la EPS", "Fecha registro", "Año"));


            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);

            CellStyle centeredStyle = workbook.createCellStyle();
            centeredStyle.setAlignment(HorizontalAlignment.CENTER);

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            int rowIdx = 1;
            int indexRowPregunta = 0;
            for (RegistroVMA registro : registros) {
                Row row = sheet.createRow(rowIdx++);
                agregarCelda(0, row, centeredStyle, String.valueOf(rowIdx-1));
                agregarCelda(1, row, centeredStyle, registro.getEmpresa().getNombre());
                agregarCelda(2, row, centeredStyle, registro.getEmpresa().getTipo());
                agregarCelda(3, row, centeredStyle, formatter.format(registro.getCreatedAt()));
                agregarCelda(4, row, centeredStyle, registro.getFichaRegistro().getAnio());

                CuestionarioDTO cuestionario = cuestionarioService.getCuestionarioConRespuestas(registro.getIdRegistroVma());
                List<PreguntaDTO> preguntas = cuestionario.getSecciones().stream().map(SeccionDTO::getPreguntas).flatMap(List::stream).collect(Collectors.toList());

                int columnaIndex = 4;
                for (PreguntaDTO pregunta: preguntas) {
                    columnaIndex++;
                    if(indexRowPregunta == 0) {
                        headersList.add(pregunta.getDescripcion());
                    }

                    switch(pregunta.getTipoPregunta()) {
                        case TEXTO:
                        case RADIO:
                            agregarCelda(columnaIndex, row, centeredStyle, getRepuesta(pregunta.getRespuestaDTO()));
                            break;
                        case NUMERICO:
                            StringBuilder respuesta = new StringBuilder();
                            if(!pregunta.getAlternativas().isEmpty()) {
                                for (AlternativaDTO altenativa: pregunta.getAlternativas()) {
                                    respuesta.append(altenativa.getNombreCampo()).append(": ").append(getRepuesta(altenativa.getRespuestaDTO())).append("; ");
                                }
                            }else {
                                respuesta.append(getRepuesta(pregunta.getRespuestaDTO()));
                            }

                            agregarCelda(columnaIndex, row, centeredStyle, respuesta.toString());
                            break;
                        case ARCHIVO:
                            String respuestaArchivo = "";
                            if(Objects.nonNull(pregunta.getRespuestaDTO())) {
                                respuestaArchivo = "SÍ SUBIÓ";
                            }else {
                                respuestaArchivo = "NO SUBIÓ";
                            }
                            agregarCelda(columnaIndex, row, centeredStyle, respuestaArchivo);
                            break;
                        default:
                            break;
                    }
                }
                indexRowPregunta++;
            }

            for (int i = 0; i < headersList.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headersList.get(i));
                cell.setCellStyle(headerStyle);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el excel" + e.getMessage());
        }
    }

    private String getRepuesta(RespuestaDTO respuestaDTO) {
        return Objects.nonNull(respuestaDTO) ? respuestaDTO.getRespuesta() : " ";
    }

    private void agregarCelda(int columna, Row row, CellStyle cellStyle, String valor) {
        Cell cell6 = row.createCell(columna);
        cell6.setCellValue(valor);
        cell6.setCellStyle(cellStyle);
    }

}