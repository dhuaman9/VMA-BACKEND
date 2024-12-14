package pe.gob.sunass.vma.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.sunass.vma.dto.EmpresaDTO;
import pe.gob.sunass.vma.model.TipoEmpresa;
import pe.gob.sunass.vma.exception.FailledValidationException;
import pe.gob.sunass.vma.service.EmpresaService;

@RestController
@RequestMapping("/empresa")
public class EmpresaController {

	private static Logger logger = LoggerFactory.getLogger(EmpresaController.class);

	@Autowired
	private EmpresaService empresaService;

	public EmpresaController() {
		super();
	}

	@GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getList() {
		ResponseEntity<?> response = null;

		try {
			List<EmpresaDTO> list = this.empresaService.findAll();

			if (list.size() == 0) {
				response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
			} else {
				response = new ResponseEntity<List<EmpresaDTO>>(list, HttpStatus.OK);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}
	
//	@GetMapping(path = "/tipoEmpresas", produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<?> getListTipoEmpresa() {
//		ResponseEntity<?> response = null;
//
//		try {
//			List<TipoEmpresa> list = this.empresaService.findAllTipoEmpresas();
//
//			if (list.size() == 0) {
//				response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
//			} else {
//				response = new ResponseEntity<List<TipoEmpresa>>(list, HttpStatus.OK);
//			}
//		} catch (Exception ex) {
//			logger.error(ex.getMessage(), ex);
//			response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//
//		return response;
//	}

	@GetMapping(path = "/listarPaginado", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getList(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "filter", required = false) String filter) {

		ResponseEntity<?> response = null;

		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<EmpresaDTO> empresaPage;

			if (filter != null && !filter.isEmpty()) {
				empresaPage = this.empresaService.findByFilter(filter, pageable);
			} else {
				empresaPage = this.empresaService.findAll(pageable);
			}

			if (empresaPage.isEmpty()) {
				response = new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
			} else {
				response = new ResponseEntity<>(empresaPage, HttpStatus.OK);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> findById(@PathVariable(name = "id") Integer id) {
		ResponseEntity<?> response = null;

		try {
			EmpresaDTO dto = this.empresaService.findById(id, true);

			if (dto == null) {
				response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
			} else {
				response = new ResponseEntity<EmpresaDTO>(dto, HttpStatus.OK);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> registrar(@RequestBody EmpresaDTO request) {
		ResponseEntity<?> response = null;

		try {
			EmpresaDTO dto = this.empresaService.registrar(request);
			response = new ResponseEntity<>(dto, HttpStatus.CREATED);
		} catch (FailledValidationException ex) { // BadRequestException ex

			throw ex;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}

	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody EmpresaDTO request) {
		ResponseEntity<?> response = null;

		try {
			EmpresaDTO dto = this.empresaService.update(request);

			if (dto == null) {
				response = new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
			} else {
				response = new ResponseEntity<EmpresaDTO>(dto, HttpStatus.ACCEPTED);
			}
		} catch (FailledValidationException ex) { // BadRequestException ex

			throw ex;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}


}
