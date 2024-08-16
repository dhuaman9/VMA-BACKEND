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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.dto.UsuarioDTO;
import pe.gob.sunass.vma.exception.FailledValidationException;
import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.service.UsuarioService;


@RestController
@RequestMapping("/usuario")
public class UsuarioController {
	
	 private static Logger logger = LoggerFactory.getLogger(UsuarioController.class);
	 
	 @Autowired
	  private UsuarioService usuarioService;

	  public UsuarioController() {
	    super();
	  }

	  @GetMapping(path="/listar",
	              produces=MediaType.APPLICATION_JSON_VALUE)
	 // @PreAuthorize("hasAuthority('Administrador1')") //dhr
	  public ResponseEntity<?> getList() {
	    ResponseEntity<?> response = null;

	    
	    try {
	      List<UsuarioDTO> list = this.usuarioService.findAll();

	      if (list.size() == 0) {
	        response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
	      }
	      else {
	        response = new ResponseEntity<List<UsuarioDTO>>(list, HttpStatus.OK);
	      }
	    }
	    catch (Exception ex) {
	      logger.error(ex.getMessage(), ex);
	      response = new ResponseEntity<String>( ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    

	    return response;
	  }

	  
	  //obtener usuarios del ldap
	 @GetMapping(path="/listarUsuariosLDAP",
	              produces=MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<?> listarUsuariosLDAP() {
	    ResponseEntity<?> response = null;
	
	
	    try {
	      List<UsuarioDTO> list = this.usuarioService.obtenerUsuariosLdap();
	      
	      //pendiente usar el otro metodo 
	      //logger.info("obtener count lista ldap2, metodo 2 : "+usuarioService.obtenerUsuariosLdap2().size());
	    
	
	      if (list.size() == 0) {
	        response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
	      }
	      else {
	        response = new ResponseEntity<List<UsuarioDTO>>(list, HttpStatus.OK);
	      }
	    }
	    catch (Exception ex) {
	      logger.error(ex.getMessage(), ex);
	      response = new ResponseEntity<String>( ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    
	    return response;
	  }

	  @GetMapping(path="/page/{num}/{size}",
	              produces=MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<?> getPage(@PathVariable(name="num") Integer num,
	                                   @PathVariable(name="size") Integer size ) {
	    ResponseEntity<?> response = null;

	    try {
	      Pageable pageable = PageRequest.of(num - 1, size);
	      Page<UsuarioDTO> page = this.usuarioService.findAll(pageable);

	      if (page == null || page.getContent().size() == 0) {
	        response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
	      }
	      else {
	        response = new ResponseEntity<Page<UsuarioDTO>>(page, HttpStatus.OK);
	      }
	    }
	    catch (Exception ex) {
	      logger.error(ex.getMessage(), ex);
	      response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
	                                             HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	    return response;
	  }

	  @GetMapping(path="/findbyid/{id}",
	              produces=MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<?> findById(@PathVariable(name="id") Integer id) {
	    ResponseEntity<?> response = null;

	    try {
	    	UsuarioDTO dto = this.usuarioService.findById(id);
	    	 logger.info("findById : "+dto);

	      if (dto == null) {
	        response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
	      }
	      else {
	        response = new ResponseEntity<UsuarioDTO>(dto, HttpStatus.OK);
	        logger.info("response : "+ response);
	      }
	    }
	    catch (Exception ex) {
	      logger.error(ex.getMessage(), ex);
	      response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
	                                             HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    

	    return response;
	  }

	  @PostMapping(produces=MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<?> registrar(@RequestBody UsuarioDTO request)  {
	    ResponseEntity<?> response = null;
	   
		    try {
		    	UsuarioDTO dto = this.usuarioService.registrar(request);
		      response = new ResponseEntity<>(dto,HttpStatus.CREATED);
		    }
		    catch (FailledValidationException ex) {  //BadRequestException ex
		    	
		    	throw ex;
		    }
		    catch (Exception ex) {
			      logger.error(ex.getMessage(), ex);
			      response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
			 }
		    
	    return response;
	  }

	  @PutMapping(produces=MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<?> update(@RequestBody UsuarioDTO request) {
	    ResponseEntity<?> response = null;

	    try {
	    	UsuarioDTO dto = this.usuarioService.update(request);

        if (dto == null) {
	        response = new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
	      else {
	        response = new ResponseEntity<UsuarioDTO>(dto,
	                                                  HttpStatus.ACCEPTED);
	      }
	    }
	    catch (FailledValidationException ex) {
	    	throw ex;
	    }
	    catch (Exception ex) {
	      logger.error(ex.getMessage(), ex);
	      //throw ex;
	      response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    
//
	    return response;
	  }
	  
	  //endpoint para obtener el rol segun el userName , para usarlo al  menu dinamico .
	  
//	  @GetMapping(path="/findByUserName/{userName}",
//	          produces=MediaType.APPLICATION_JSON_VALUE)
//	  public ResponseEntity<?> findByUserName(@PathVariable(name="userName") String userName) {
//	    ResponseEntity<?> response = null; 
//
//	    logger.info("findByUserName");
//	    logger.info(Constants.Logger.Method.Initialize);
//
//	    try {
//	      UsuarioDTO usuarioDTO = this.usuarioService.findByUserName(userName, false);
//
//	      if (usuarioDTO == null) {
//	        response = new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
//	      }
//	      else {
//	        response = new ResponseEntity<UsuarioDTO>(usuarioDTO, HttpStatus.OK);
//	      }
//	    }
//	    catch (Exception ex) {
//	      logger.error(ex.getMessage(), ex);
//	      response = new ResponseEntity<String>("{\"error\" : \"" + ex.getMessage() + "\"}",
//	              HttpStatus.INTERNAL_SERVER_ERROR);
//	    }
//	    finally {
//	      logger.info(Constants.Logger.Method.Finalize);
//	    }
//
//	    return response;
//	  }


}
