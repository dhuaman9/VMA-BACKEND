package pe.gob.sunass.vma.security.controller;

import java.text.ParseException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.sunass.vma.exception.LocalNotFoundException;
import pe.gob.sunass.vma.security.dto.AuthenticationRequestDTO;
import pe.gob.sunass.vma.security.dto.AuthenticationResponseDTO;
import pe.gob.sunass.vma.security.dto.JwtTokenDTO;
import pe.gob.sunass.vma.security.service.AuthenticationService;
import pe.gob.sunass.vma.util.DefaultMessage;
import pe.gob.sunass.vma.util.ResponseEntity;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
    private AuthenticationService authenticationService;
	
//	@Autowired
//	private UserAuthenticationManager authManager;
//	
//	@Autowired
//	private HttpServletRequest request;
//	
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> login(
            @RequestBody @Valid AuthenticationRequestDTO authRequest){

		try {
			
			AuthenticationResponseDTO jwtDto = authenticationService.login(authRequest);
			
			ResponseEntity<?> response = new ResponseEntity<>();
			response.setValue(jwtDto.getJwt());
			
			return response;
	        
		} catch (Exception ex) {

			if (ex instanceof InternalAuthenticationServiceException)
				return ResponseEntity.error(DefaultMessage.Error.customMessage(ex.getMessage()));
			return ResponseEntity.error(DefaultMessage.Error.customMessage("Ocurrió un error al autenticarse"));
		}
		
    }
	
	@RequestMapping(value = "/refresh-token", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> refreshToken(
			@RequestBody @Valid JwtTokenDTO jwtToken) throws ParseException{
		
		try {
			
			AuthenticationResponseDTO jwtDto = authenticationService.refreshToken(jwtToken);
			
			ResponseEntity<?> response = new ResponseEntity<>();
			response.setValue(jwtDto.getJwt());

			return response;
		
		} catch (Exception ex) {

			if (ex instanceof InternalAuthenticationServiceException)
				return ResponseEntity.error(DefaultMessage.Error.customMessage(ex.getMessage()));
			return ResponseEntity.error(DefaultMessage.Error.customMessage("Ocurrió un error de autenticacion"));
		}
	}
	
//	@RequestMapping(value = "/admin-access")
//	public String adminAccess(){
//		return "hola";
//	}
//	
//	@PreAuthorize("hasAuthority('Administrador OTI')")
//	@RequestMapping(value = "/admin-test")
//	public String test() throws LocalNotFoundException{
//		
//		if(authenticationService.getUsername().equals("wrivera")){
//			throw new LocalNotFoundException("es wrivera");
//		}
//		
//		return "hola";
//	}
}
