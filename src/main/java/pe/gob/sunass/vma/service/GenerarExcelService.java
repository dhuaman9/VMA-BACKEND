package pe.gob.sunass.vma.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.controller.EmpresaController;
import pe.gob.sunass.vma.dto.AlternativaDTO;
import pe.gob.sunass.vma.dto.CuestionarioDTO;
import pe.gob.sunass.vma.dto.PreguntaDTO;
import pe.gob.sunass.vma.dto.RespuestaDTO;
import pe.gob.sunass.vma.dto.SeccionDTO;
import pe.gob.sunass.vma.model.Empresa;
import pe.gob.sunass.vma.model.FichaRegistro;
import pe.gob.sunass.vma.model.TipoEmpresa;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;
import pe.gob.sunass.vma.repository.EmpresaRepository;
import pe.gob.sunass.vma.repository.RegistroVMARepository;

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

	public ByteArrayInputStream generarExcelCuestionario(List<Integer> registrosIds, Integer eps, String estado,
			String anio, Date fechaDesde, Date fechaHasta, String busquedaGlobal) {

		List<RegistroVMA> registros = new ArrayList<RegistroVMA>();

		if (registrosIds == null || registrosIds.size() == 0) {

			registros = this.findRegistrosVmasFiltrados(eps, estado, anio, fechaDesde, fechaHasta, busquedaGlobal);

		} else {
			registros = registroVMARepository.findRegistrosVmasPorIds(registrosIds);
		}

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Registros VMA");

			Row headerRow = sheet.createRow(0);
			List<String> headersList = new ArrayList<>(
					Arrays.asList("N°", "Empresa EPS", "Tamaño de la EPS", "Estado", "Fecha de registro", "Año"));

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
				agregarCelda(0, row, centeredStyle, String.valueOf(rowIdx - 1));
				agregarCelda(1, row, centeredStyle, registro.getEmpresa().getNombre());
				agregarCelda(2, row, centeredStyle, registro.getEmpresa().getTipoEmpresa().getNombre()); //dhr c
				agregarCelda(3, row, centeredStyle,
						registro.getEstado() == null ? "SIN REGISTRO" : registro.getEstado());
				agregarCelda(4, row, centeredStyle,
						registro.getCreatedAt() == null ? "" : formatter.format(registro.getCreatedAt()));
				agregarCelda(5, row, centeredStyle, registro.getFichaRegistro().getAnio());

				if (registro.getIdRegistroVma() != null) {

					CuestionarioDTO cuestionario = cuestionarioService
							.getCuestionarioConRespuestas(registro.getIdRegistroVma());
					List<PreguntaDTO> preguntas = cuestionario.getSecciones().stream().map(SeccionDTO::getPreguntas)
							.flatMap(List::stream).collect(Collectors.toList());

					int columnaIndex = 5;
					for (PreguntaDTO pregunta : preguntas) {
						columnaIndex++;
						if (indexRowPregunta == 0) {
							headersList.add(pregunta.getDescripcion());
						}

						switch (pregunta.getTipoPregunta()) {
						case TEXTO:
						case RADIO:
							agregarCelda(columnaIndex, row, centeredStyle, getRepuesta(pregunta.getRespuestaDTO()));
							break;
						case NUMERICO:
							StringBuilder respuesta = new StringBuilder();
							if (!pregunta.getAlternativas().isEmpty()) {
								for (AlternativaDTO altenativa : pregunta.getAlternativas()) {
									respuesta.append(altenativa.getNombreCampo()).append(": ")
											.append(getRepuesta(altenativa.getRespuestaDTO())).append("; ");
								}
							} else {
								respuesta.append(getRepuesta(pregunta.getRespuestaDTO()));
							}

							agregarCelda(columnaIndex, row, centeredStyle, respuesta.toString());
							break;
						case ARCHIVO:
							String respuestaArchivo = "";
							if (Objects.nonNull(pregunta.getRespuestaDTO())) {
								respuestaArchivo = "SÍ SUBIÓ";
							} else {
								respuestaArchivo = "NO SUBIÓ";
							}
							agregarCelda(columnaIndex, row, centeredStyle, respuestaArchivo);
							break;
						default:
							break;
						}
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

	/**
	 * Consulta de VMAs para la generacion del excel
	 * 
	 * @param empresaId
	 * @param estado
	 * @param year
	 * @param startDate
	 * @param endDate
	 * @param search
	 * @return
	 */
	public List<RegistroVMA> findRegistrosVmasFiltrados(Integer empresaId, String estado, String year, Date startDate,
			Date endDate, String search) {

		try {

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

			Root<Empresa> empresaRoot = query.from(Empresa.class);
			Root<FichaRegistro> fichaRegistro = query.from(FichaRegistro.class);

			Join<Empresa, RegistroVMA> registroVMA = empresaRoot.join("registrosVMA", JoinType.LEFT);
			registroVMA.on(cb.equal(registroVMA.get("fichaRegistro").get("idFichaRegistro"),
					fichaRegistro.get("idFichaRegistro")));

		
			query.multiselect(empresaRoot.get("idEmpresa"), empresaRoot.get("nombre"), empresaRoot.get("regimen"),
					empresaRoot.get("tipoEmpresa").get("nombre"), registroVMA.get("idRegistroVma"), registroVMA.get("estado"),
					registroVMA.get("createdAt"), fichaRegistro.get("anio"));

			List<Predicate> predicates = new ArrayList<>();

			if (empresaId != null) {
				predicates.add(cb.equal(empresaRoot.get("idEmpresa"), empresaId));
			}
			if (estado != null) {
				if (estado.equals(Constants.ESTADO_SIN_REGISTRO)) {
					predicates.add(cb.isNull(registroVMA.get("estado")));
				} else {
					predicates.add(cb.equal(registroVMA.get("estado"), estado));
				}

			}
			if (startDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(registroVMA.get("createdAt"), startDate));
			}
			if (endDate != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(endDate);
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
				calendar.set(Calendar.MILLISECOND, 999);
				predicates.add(cb.lessThanOrEqualTo(registroVMA.get("createdAt"), calendar.getTime()));
			}
			if (year != null) {
				predicates.add(cb.equal(fichaRegistro.get("anio"), year));
			}
			if (search != null) {
				Predicate namePredicate = cb.like(cb.lower(empresaRoot.get("nombre")),
						"%" + search.toLowerCase() + "%");
				Join<Empresa, TipoEmpresa> tipoEmpresaJoin = empresaRoot.join("tipoEmpresa");
				Predicate typePredicate = cb.like(
				    cb.lower(tipoEmpresaJoin.get("nombre")),
				    "%" + search.toLowerCase() + "%"
				);
				
//				Predicate typePredicate = cb.like(cb.lower(empresaRoot.get("tipo")), "%" + search.toLowerCase() + "%");
				
				Predicate regimenPredicate = cb.like(cb.lower(empresaRoot.get("regimen")),
						"%" + search.toLowerCase() + "%");
				Predicate estadoPredicate = cb.like(cb.lower(registroVMA.get("estado")),
						"%" + search.toLowerCase() + "%");
				Predicate anioPredicate = cb.like(cb.lower(fichaRegistro.get("anio")),
						"%" + search.toLowerCase() + "%");
				predicates.add(cb.or(namePredicate, typePredicate, regimenPredicate, estadoPredicate, anioPredicate));
			}

			query.where(predicates.toArray(new Predicate[0]));
			query.orderBy(cb.desc(cb.coalesce(registroVMA.get("idRegistroVma"), 0)), cb.asc(empresaRoot.get("nombre")),
					cb.desc(fichaRegistro.get("anio")));

			TypedQuery<Object[]> typedQuery = entityManager.createQuery(query);

			List<RegistroVMA> resultList = typedQuery.getResultList().stream().map(objeto -> {
				Empresa emp = new Empresa();
				emp.setIdEmpresa((Integer) objeto[0]);
				emp.setNombre((String) objeto[1]);
				emp.setRegimen((String) objeto[2]);
				//emp.setTipo((String) objeto[3]);
				
				String nombreTipo = (String) objeto[3];
				TipoEmpresa tipoEmpresa = empresaRepository.findTipoEmpresaByNombre(nombreTipo);
				emp.setTipoEmpresa(tipoEmpresa);
				//emp.setTipoEmpresa((TipoEmpresa) objeto[3]);
				
				FichaRegistro fReg = new FichaRegistro();
				fReg.setAnio((String) objeto[7]);
				RegistroVMA rVMA = new RegistroVMA();
				rVMA.setIdRegistroVma((Integer) objeto[4]);
				rVMA.setEstado((String) objeto[5]);
				rVMA.setCreatedAt((Date) objeto[6]);
				rVMA.setEmpresa(emp);
				rVMA.setFichaRegistro(fReg);
				return rVMA;
			}).collect(Collectors.toList());

			return resultList;

		} catch (Exception e) {
			// Manejar la excepción según sea necesario
			return Collections.emptyList(); // O lanza una excepción según sea necesario
		}

	}

}