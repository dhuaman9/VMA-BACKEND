package pe.gob.sunass.vma.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.sunass.vma.controller.EmpresaController;
import pe.gob.sunass.vma.dto.*;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;
import pe.gob.sunass.vma.repository.EmpresaRepository;
import pe.gob.sunass.vma.repository.RegistroVMARepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.Query;

import javax.persistence.EntityManager;

@Service
public class GenerarExcelService {

	private static Logger logger = LoggerFactory.getLogger(EmpresaController.class);
	
    @Autowired
    private RegistroVMARepository registroVMARepository;

    @Autowired
    private CuestionarioService cuestionarioService;
    
    @Autowired
    private EmpresaRepository empresaRepository;
    
    @Autowired
    private EntityManager entityManager;

    public ByteArrayInputStream generarExcelCuestionario(List<Integer> registrosIds, Integer eps, String estado, String anio, Date fechaDesde, Date fechaHasta, String busquedaGlobal) {
    
   
    
     	List<RegistroVMA> registros =new ArrayList<RegistroVMA>();
     	
     	if (registrosIds == null || registrosIds.size() ==0 ) {
     		
     		 logger.info("EPS: " + eps);
             logger.info("Estado: " + estado);
             logger.info("Año: " + anio);
             logger.info("Fecha Desde: " + fechaDesde);
             logger.info("Fecha Hasta: " + fechaHasta);
             logger.info("Búsqueda Global: " + busquedaGlobal);
     		
     		
             registros=this.findRegistrosVmasFiltrados(eps, estado, anio, fechaDesde, fechaHasta, busquedaGlobal);
 		} else {
 			registros = registroVMARepository.findRegistrosVmasPorIds(registrosIds);
 		}

        
        logger.info("Registros filtrados: " + registros.size());
        logger.info("IDs: " + registrosIds);
        logger.info("EPS: " + eps);
        logger.info("Estado: " + estado);
        logger.info("Año: " + anio);
        logger.info("Fecha Desde: " + fechaDesde);
        logger.info("Fecha Hasta: " + fechaHasta);
        logger.info("Búsqueda Global: " + busquedaGlobal);
     	
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Registros VMA");

            Row headerRow = sheet.createRow(0);
            List<String> headersList = new ArrayList<>(Arrays.asList("N°", "Empresa EPS", "Tamaño de la EPS", "Estado","Fecha de registro", "Año"));

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
                agregarCelda(3, row, centeredStyle, registro.getEstado());
                agregarCelda(4, row, centeredStyle, formatter.format(registro.getCreatedAt()));
                agregarCelda(5, row, centeredStyle, registro.getFichaRegistro().getAnio());


                CuestionarioDTO cuestionario = cuestionarioService.getCuestionarioConRespuestas(registro.getIdRegistroVma());
                List<PreguntaDTO> preguntas = cuestionario.getSecciones().stream().map(SeccionDTO::getPreguntas).flatMap(List::stream).collect(Collectors.toList());

                int columnaIndex = 5;
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
    
    
    
    public ByteArrayInputStream generarExcelEPSSinRegistro() {
    	
        List<Object[]> missingFichaRegistros = empresaRepository.findMissingFichaRegistros();  

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Registros VMA");

            Row headerRow = sheet.createRow(0);
            List<String> headersList = new ArrayList<>(Arrays.asList("N°", "Empresa EPS","Tamaño", "Estado", "Año" , "Periodo de registro"));


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
           
            SimpleDateFormat formatFechaInicioFin = new SimpleDateFormat("dd-MM-yyyy");
            	
            	 for (Object[] item: missingFichaRegistros) {
            		 Row row = sheet.createRow(rowIdx++);
                     agregarCelda(0, row, centeredStyle, String.valueOf(rowIdx-1));  // correlativo  - N°
                     agregarCelda(1, row, centeredStyle, (String) item[0]);   // EPS
                     agregarCelda(2, row, centeredStyle, (String) item[1]);   //tamanio
                     agregarCelda(3, row, centeredStyle, "SIN REGISTRO"); //  Estado
                     agregarCelda(4, row, centeredStyle, (String) item[2]); //  anio
                     agregarCelda(5, row, centeredStyle, formatFechaInicioFin.format(item[3]) +"  al  " +formatFechaInicioFin.format(item[4]) );  //periodo de registro
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
    
    public List<RegistroVMA> findRegistrosVmasFiltrados(
            Integer eps,
            String estado,
            String anio,
            Date fechaDesde,
            Date fechaHasta,
            String busquedaGlobal) {

        StringBuilder queryBuilder = new StringBuilder("FROM RegistroVMA r WHERE 1=1");
        
        // Usar una lista para almacenar los parámetros
        Map<String, Object> params = new HashMap<>();


        if (eps != null) {
            queryBuilder.append(" AND r.empresa.idEmpresa = :eps");
            params.put("eps", eps);
        }
        if (estado != null) {
            queryBuilder.append(" AND r.estado = :estado");
            params.put("estado", estado);
        }
        if (anio != null) {
            queryBuilder.append(" AND r.fichaRegistro.anio = :anio");
            params.put("anio", anio);
        }
        if (fechaDesde != null) {
            queryBuilder.append(" AND r.createdAt >= :fechaDesde");
            // Convertir LocalDate a Date
           // Date fechaDesdeDate = Date.from(fechaDesde.atStartOfDay(ZoneId.systemDefault()).toInstant());
            params.put("fechaDesde", fechaDesde);
        }
        if (fechaHasta != null) {
            queryBuilder.append(" AND r.createdAt <= :fechaHasta");
            // Convertir LocalDate a Date ya no es necesario
            //Date fechaHastaDate = Date.from(fechaHasta.atStartOfDay(ZoneId.systemDefault()).toInstant());
            params.put("fechaHasta", fechaHasta);
        }
        if (busquedaGlobal != null) {
            queryBuilder.append(" AND (LOWER(r.empresa.nombre) LIKE LOWER(CONCAT('%', :busquedaGlobal, '%')) "
                    + "OR LOWER(r.empresa.tipo) LIKE LOWER(CONCAT('%', :busquedaGlobal, '%'))" 
                    + "OR LOWER(r.empresa.regimen) LIKE LOWER(CONCAT('%', :busquedaGlobal, '%'))" 
                    + "OR LOWER(r.estado) LIKE LOWER(CONCAT('%', :busquedaGlobal, '%'))" 
            		+" OR LOWER(r.fichaRegistro.anio) LIKE LOWER(CONCAT('%', :busquedaGlobal, '%'))  )");
            params.put("busquedaGlobal", busquedaGlobal);
        }

        // Crear la consulta
        Query query = entityManager.createQuery(queryBuilder.toString(), RegistroVMA.class);

        // Establecer los parámetros en la consulta
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        try {
            return query.getResultList();
        } catch (Exception e) {
            // Manejar la excepción según sea necesario
            return Collections.emptyList(); // O lanza una excepción según sea necesario
        }
    }

}