package pe.gob.sunass.vma.controller;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import pe.gob.sunass.vma.dto.FichaDTO;
import pe.gob.sunass.vma.dto.RegistroVMADTO;
import pe.gob.sunass.vma.dto.RegistroVMAFilterDTO;
import pe.gob.sunass.vma.dto.RegistroVMARequest;
import pe.gob.sunass.vma.exception.FailledValidationException;
import pe.gob.sunass.vma.exception.ForbiddenException;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;
import pe.gob.sunass.vma.security.jwt.JWTProvider;
import pe.gob.sunass.vma.service.GenerarExcelService;
import pe.gob.sunass.vma.service.RegistroVMAService;

@RestController
@RequestMapping("/registroVMA")
public class RegistroVMAController {

	private static Logger logger = LoggerFactory.getLogger(EmpresaController.class);

	@Autowired
	private RegistroVMAService registroVMAService;

	@Autowired
	private JWTProvider jwtProvider;

	@Autowired
	private GenerarExcelService excelService;

	public RegistroVMAController() {
		super();
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> saveRegistroVMA(@RequestBody RegistroVMARequest request,
			@RequestHeader("Authorization") String token) {
		Integer registroVMAId = registroVMAService.saveRegistroVMA(null, request, getUsername(token));
		return new ResponseEntity<>(registroVMAId, HttpStatus.CREATED);
	}

	@PutMapping(path = "/{idRegistroVMA}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> saveRegistroVMA(@PathVariable Integer idRegistroVMA,
			@RequestBody RegistroVMARequest request) {
		Integer registroVMAId = registroVMAService.saveRegistroVMA(idRegistroVMA, request, null);
		return new ResponseEntity<>(registroVMAId, HttpStatus.CREATED);
	}

	@GetMapping(path = "/activo", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEstadoRegistro(@RequestHeader("Authorization") String token) {
		boolean registroCompletado = registroVMAService.isRegistroCompletado(getUsername(token)); //// para deshabilitar
																									//// o habilitar el
																									//// boton de
																									//// Registrar VMA
		return new ResponseEntity<>(registroCompletado, HttpStatus.OK);
	}

	/*@GetMapping(path = "/listar", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getList(@RequestHeader("Authorization") String token) {
		ResponseEntity<?> response = null;

		try {
			response = new ResponseEntity<List<RegistroVMADTO>>(
					this.registroVMAService.findAllOrderById(getUsername(token)), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}*/

	@GetMapping("/search")
	public Page<RegistroVMA> searchRegistroVMA(@RequestParam(required = false) Integer empresaId,
			@RequestParam(required = false) String estado,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
			@RequestParam(required = false) String year, @RequestParam(name = "page", defaultValue = "0") Integer page,
			@RequestParam(name = "size", defaultValue = "8") Integer size,
			@RequestParam(required = false) String search, @RequestHeader("Authorization") String token) {

		return registroVMAService.searchRegistroVMA(empresaId, estado, startDate, endDate, year, getUsername(token),
				page, size, search);
	}

	@GetMapping(path = "/findByid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> findById(@PathVariable(name = "id") Integer id) {
		ResponseEntity<?> response = null;

		try {
			RegistroVMADTO dto = this.registroVMAService.findById(id);

			if (dto == null) {
				response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
			} else {

				response = new ResponseEntity<RegistroVMADTO>(dto, HttpStatus.OK);
			}
		} catch (ForbiddenException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}

	private String getUsername(String token) {
		token = token.split(" ")[1];
		return jwtProvider.extractUsername(token);
	}

	@GetMapping("/filter") // no se esta utilizando.
	public List<RegistroVMA> filterRegistros(RegistroVMAFilterDTO filterDTO) {
		return registroVMAService.filterRegistros(filterDTO);
	}

	@GetMapping("/reporte-registros-vma")
	@PreAuthorize("hasAnyAuthority('ADMINISTRADOR DF','CONSULTOR')")
	public ResponseEntity<byte[]> descargarReporteVMA(@RequestParam(required = false) List<Integer> idsVma,
			@RequestParam(required = false) Integer eps, @RequestParam(required = false) String estado,
			@RequestParam(required = false) String anio,
			@RequestParam(required = false, name = "fechaDesde") String fechaDesdeString,
			@RequestParam(required = false, name = "fechaHasta") String fechaHastaString,
			@RequestParam(required = false) String busquedaGlobal) throws ParseException {

		// Conversión de fechas
		SimpleDateFormat simpleFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date fechaDesde = null;
		if (fechaDesdeString != null) {
			fechaDesde = simpleFormat.parse(fechaDesdeString);

		}

		Date fechaHasta = null;
		if (fechaHastaString != null) {
			fechaHasta = simpleFormat.parse(fechaHastaString);

		}

		// Llama al servicio para generar el archivo Excel, pasando los filtros
		ByteArrayInputStream byteArrayExcel = excelService.generarExcelCuestionario(idsVma, eps, estado, anio,
				fechaDesde, fechaHasta, busquedaGlobal);

		// Configurar las cabeceras de respuesta
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=registros_vma.xlsx");

		return ResponseEntity.ok().headers(headers).body(byteArrayExcel.readAllBytes());
	}

	@PutMapping("/estado-incompleto/{id}")
	public ResponseEntity<Void> actualizarEstadoIncompleto(@PathVariable Integer id) {
		this.registroVMAService.actualizarEstadoIncompleto(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/alerta-para-eps-sin-registrar")
	public ResponseEntity<RegistroVMADTO> obtenerRegistroVMASinCompletar() throws Exception {
		RegistroVMADTO dto = registroVMAService.obtenerEmpresaSinCompletarRegistroVMA();

		if (dto == null) {

			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.ok(dto);
	}

}