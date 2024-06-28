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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.dto.EmpresaDTO;
import pe.gob.sunass.vma.model.Usuario;
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

	
	  @GetMapping(path="/list",
	              produces=MediaType.APPLICATION_JSON_VALUE)
	  //@PreAuthorize("hasAuthority('Administrador2')") //dhr
	  public ResponseEntity<?> getList() {
	    ResponseEntity<?> response = null;

	    logger.info(Constants.Logger.Method.Initialize);

	    try {
	      List<EmpresaDTO> list = this.empresaService.findAll();

	      if (list.size() == 0) {
	        response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
	      }
	      else {
	        response = new ResponseEntity<List<EmpresaDTO>>(list, HttpStatus.OK);
	      }
	    }
	    catch (Exception ex) {
	      logger.error(ex.getMessage(), ex);
	      response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
	                                             HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    finally {
	      logger.info(Constants.Logger.Method.Finalize);
	    }

	    return response;
	  }


	  @GetMapping(path="/page/{num}/{size}",
	              produces=MediaType.APPLICATION_JSON_VALUE)
	
	  public ResponseEntity<?> getPage(@PathVariable(name="num") Integer num,
	                                   @PathVariable(name="size") Integer size) {
	    ResponseEntity<?> response = null;
	    
	    logger.info(Constants.Logger.Method.Initialize);

	    try {
	      Pageable pageable = PageRequest.of(num - 1, size);
	      Page<EmpresaDTO> page = this.empresaService.findAll(pageable);

	      if (page == null || page.getContent().size() == 0) {
	        response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
	      }
	      else {
	        response = new ResponseEntity<Page<EmpresaDTO>>(page, HttpStatus.OK);
	      }
	    }
	    catch (Exception ex) {
	      logger.error(ex.getMessage(), ex);
	      response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
	                                             HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    finally {
	     // logger.info(" > " + DateUtil.timeElapsed(System.currentTimeMillis() - startProcess) + " took");
	      logger.info(Constants.Logger.Method.Finalize);
	    }

	    return response;
	  }

	  @GetMapping(path="/findbyid/{id}",
	              produces=MediaType.APPLICATION_JSON_VALUE)
	 
	  public ResponseEntity<?> findById(@PathVariable(name="id") Integer id) {
	    ResponseEntity<?> response = null;
	    long startProcess = System.currentTimeMillis();

	    logger.info(Constants.Logger.Method.Initialize);

	    try {
	    	EmpresaDTO dto = this.empresaService.findById(id, true);

	      if (dto == null) {
	        response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
	      }
	      else {
	        response = new ResponseEntity<EmpresaDTO>(dto, HttpStatus.OK);
	      }
	    }
	    catch (Exception ex) {
	      logger.error(ex.getMessage(), ex);
	      response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
	                                             HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    finally {
	      logger.info(Constants.Logger.Method.Finalize);
	    }

	    return response;
	  }

	  @PostMapping(produces=MediaType.APPLICATION_JSON_VALUE)
	
	  public ResponseEntity<?> registrar(@RequestBody EmpresaDTO request) {
	    ResponseEntity<?> response = null;
	    //long startProcess = System.currentTimeMillis();

	    logger.info(Constants.Logger.Method.Initialize);

	    try {
	    	EmpresaDTO dto = this.empresaService.registrar(request);
	         response = new ResponseEntity<EmpresaDTO>(dto,
	                                                HttpStatus.CREATED);
	    }
	    catch (Exception ex) {
	      logger.error(ex.getMessage(), ex);
	      response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
	                                            HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    finally {
	       logger.info(Constants.Logger.Method.Finalize);
	    }

	    return response;
	  }

	  @PutMapping(produces=MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<?> update(@RequestBody EmpresaDTO request) {
	    ResponseEntity<?> response = null;
	    long startProcess = System.currentTimeMillis();

	    logger.info(Constants.Logger.Method.Initialize);

	    try {
	    	EmpresaDTO dto = this.empresaService.update(request);

	      if (dto == null) {
	        response = new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
	      }
	      else {
	        response = new ResponseEntity<EmpresaDTO>(dto,
	                                                  HttpStatus.ACCEPTED);
	      }
	    }
	    catch (Exception ex) {
	      logger.error(ex.getMessage(), ex);
	      response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
	                                            HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    finally {
	      logger.info(Constants.Logger.Method.Finalize);
	    }

	    return response;
	  }

//	  @DeleteMapping(path="/{id}",
//	                 produces=MediaType.APPLICATION_JSON_VALUE)
//	  public ResponseEntity<?> delete(@PathVariable(name="id") Integer id) {
//	    ResponseEntity<?> response = null;
//	    long startProcess = System.currentTimeMillis();
//
//	    logger.info(Constants.Logger.Method.Initialize);
//
//	    try {
//	      UsuarioDTO dto = this.usuarioService.delete(id);
//
//	      if (dto == null) {
//	        response = new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
//	      }
//	      else {
//	        response = new ResponseEntity<String>("{\"message\" : \"Usuario eliminado correctamente\"}",
//	                                              HttpStatus.ACCEPTED);
//	      }
//	    }
//	    catch (FailledValidationException ex) {
//	      response = new ResponseEntity<Object>("{\"error\" : \"" + ex.getMessage() + "\"}",
//	                                            HttpStatus.FAILED_DEPENDENCY);
//	    }
//	    catch (Exception ex) {
//	      logger.error(ex.getMessage(), ex);
//	      response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
//	                                            HttpStatus.INTERNAL_SERVER_ERROR);
//	    }
//	    finally {
//	      logger.info(" > " + DateUtil.timeElapsed(System.currentTimeMillis() - startProcess) + " took");
//	      logger.info(Constants.Logger.Method.Finalize);
//	    }
//
//	    return response;
//	  }

	
}
