package pe.gob.sunass.vma.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.sunass.vma.dto.SeccionDTO;
import pe.gob.sunass.vma.service.SeccionesService;

@RestController
@RequestMapping("/secciones")
public class SeccionController {

	private static Logger logger = LoggerFactory.getLogger(EmpresaController.class);

	@Autowired
	private SeccionesService seccionesService;

	@GetMapping(path = "/listar", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getList() {
		ResponseEntity<?> response = null;

		try {
			List<SeccionDTO> list = this.seccionesService.findAll();

			if (list.size() == 0) {
				response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
			} else {
				response = new ResponseEntity<List<SeccionDTO>>(list, HttpStatus.OK);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}

}
