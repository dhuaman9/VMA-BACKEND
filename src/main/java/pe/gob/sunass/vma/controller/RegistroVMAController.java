package pe.gob.sunass.vma.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.dto.RegistroVMADTO;
import pe.gob.sunass.vma.dto.RegistroVMAFilterDTO;
import pe.gob.sunass.vma.dto.RegistroVMARequest;
import pe.gob.sunass.vma.model.RegistroVMA;
import pe.gob.sunass.vma.security.jwt.JWTProvider;
import pe.gob.sunass.vma.service.RegistroVMAService;

@RestController
@RequestMapping("/registroVMA")
public class RegistroVMAController {

	private static Logger logger = LoggerFactory.getLogger(EmpresaController.class);

	@Autowired
	private RegistroVMAService registroVMAService;

	@Autowired
	private JWTProvider jwtProvider;

	public RegistroVMAController() {
		super();
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> saveRegistroVMA(@RequestBody RegistroVMARequest request,
			@RequestHeader("Authorization") String token) {
		registroVMAService.saveRegistroVMA(null, request, getUsername(token));
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping(path = "/{idRegistroVMA}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> saveRegistroVMA(@PathVariable Integer idRegistroVMA,
			@RequestBody RegistroVMARequest request) {
		registroVMAService.saveRegistroVMA(idRegistroVMA, request, null);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping(path = "/activo", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEstadoRegistro(@RequestHeader("Authorization") String token) {
		boolean registroCompletado = registroVMAService.isRegistroCompletado(getUsername(token));
		return new ResponseEntity<>(registroCompletado, HttpStatus.OK);
	}

	@GetMapping(path = "/listar", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getList(@RequestHeader("Authorization") String token) {
		ResponseEntity<?> response = null;

		logger.info(Constants.Logger.Method.Initialize);

		try {
			response = new ResponseEntity<List<RegistroVMADTO>>(
					this.registroVMAService.findAllOrderById(getUsername(token)), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
					HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			logger.info(Constants.Logger.Method.Finalize);
		}

		return response;
	}

	@GetMapping("/search")
	public List<RegistroVMA> searchRegistroVMA(
			@RequestParam(required = false) Integer empresaId,
			@RequestParam(required = false) String estado,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
			@RequestParam(required = false) String year,
			@RequestHeader("Authorization") String token) {

		return registroVMAService.searchRegistroVMA(empresaId, estado, startDate, endDate, year, getUsername(token));
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
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
					HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			logger.info(Constants.Logger.Method.Finalize);
		}

		return response;
	}

	private String getUsername(String token) {
		token = token.split(" ")[1];
		return jwtProvider.extractUsername(token);
	}
	
	 @GetMapping("/filter")
	 public List<RegistroVMA> filterRegistros(RegistroVMAFilterDTO filterDTO) {
	    return registroVMAService.filterRegistros(filterDTO);
	 }

}
