package pe.gob.sunass.vma.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.dto.EmpresaDTO;
import pe.gob.sunass.vma.dto.RegistroVMADTO;
import pe.gob.sunass.vma.dto.RegistroVMARequest;
import pe.gob.sunass.vma.dto.UsuarioDTO;
import pe.gob.sunass.vma.service.EmpresaService;
import pe.gob.sunass.vma.service.RegistroVMAService;

@RestController
@RequestMapping("/registroVMA")
public class RegistroVMAController {
	
	
	  private static Logger logger = LoggerFactory.getLogger(EmpresaController.class);
		 
	  @Autowired
	  private RegistroVMAService registroVMAService;

	  public RegistroVMAController() {
	    super();
	  }

	  @PostMapping(produces=MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<?> saveRegistroVMA(@RequestBody RegistroVMARequest request) {
		  registroVMAService.crearRegistroVMA(request);
		  return new ResponseEntity<>(HttpStatus.CREATED);
	  }
	  
	  
	@GetMapping(path="/listar",
	              produces=MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<?> getList() {
	    ResponseEntity<?> response = null;
	
	    logger.info(Constants.Logger.Method.Initialize);
	
	    try {
	      List<RegistroVMADTO> list = this.registroVMAService.findAllOrderById();
	
	      if (list.size() == 0) {
	        response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
	      }
	      else {
	        response = new ResponseEntity<List<RegistroVMADTO>>(list, HttpStatus.OK);
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

	

	  @GetMapping(path="/findByid/{id}",
	              produces=MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<?> findById(@PathVariable(name="id") Integer id) {
	    ResponseEntity<?> response = null;

	    try {
	    	RegistroVMADTO dto = this.registroVMAService.findById(id);

	      if (dto == null) {
	        response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
	      }
	      else {
	        
	        response = new ResponseEntity<RegistroVMADTO>(dto, HttpStatus.OK);
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
	  


}
