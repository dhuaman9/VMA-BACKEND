package pe.gob.sunass.vma.exception;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.AddressException;
import javax.mail.AuthenticationFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());



	@ExceptionHandler(FailledValidationException.class)
	public ResponseEntity<?> handleFailledValidationException(FailledValidationException ex) {
		return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
	}

	// si se desea usar, si sucede un conflicto al subir mas de 1 archivo con el
	// mismo nombre y casi al mismo tiempo en el formulario vma
	@ExceptionHandler(FileConflictException.class)
	public ResponseEntity<?> handleFileConflictException(FileConflictException ex) {
		logger.info("CONFLICT EXCEPTION : FileConflictException" + ex.getMessage());
		ErrorResponse errorResponse = new ErrorResponse("message", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

	}

	@ExceptionHandler(PasswordNoCambiadoException.class)
	public ResponseEntity<?> handleFileConflictException(PasswordNoCambiadoException ex) {
		logger.info("PASSWORD_NO_CAMBIADO_EXCEPTION : PasswordNoCambiadoException" + ex.getMessage());
		ErrorResponse errorResponse = new ErrorResponseToken("PASSWORD_NO_CAMBIADO_EXCEPTION", ex.getMessage(),
				ex.getToken());
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

	}

	@ExceptionHandler(LocalNotFoundException.class)
	public ResponseEntity<?> localNotFoundException(LocalNotFoundException exception) {

		Map<String, Object> customError = new HashMap<>();
		customError.put("message", exception.getMessage());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(customError);
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<?> ForbiddenException(ForbiddenException ex) {

		logger.info("PERMISO_DENEGADO : NotFoundException" + ex.getMessage());
		ErrorResponse errorResponse = new ErrorResponse("ACCESO_DENEGADO", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}

	/*@ExceptionHandler(MessagingException.class)
	public ResponseEntity<?> handleMessagingException(MessagingException ex) {
		// Devuelve un mensaje personalizado
		ErrorResponse errorResponse = new ErrorResponse("message", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	
	}

	@ExceptionHandler(ConnectException.class)
	public ResponseEntity<?> handleConnectException(ConnectException ex) {
		ErrorResponse errorResponse = new ErrorResponse("message", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
		
	}
	
	@ExceptionHandler(SendFailedException.class)
	public ResponseEntity<?> handleSendFailedException(SendFailedException ex) {
		ErrorResponse errorResponse = new ErrorResponse("message", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	@ExceptionHandler(AuthenticationFailedException.class)
	public ResponseEntity<?> handleAuthenticationFailedException(AuthenticationFailedException ex) {
		ErrorResponse errorResponse = new ErrorResponse("message", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
		
	}
	
	@ExceptionHandler(AddressException.class)
	public ResponseEntity<?> handleAddressException(AddressException ex) {
	    ErrorResponse errorResponse = new ErrorResponse("message", ex.getMessage());
	    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	*/
	
	@ExceptionHandler({ MailException.class, MessagingException.class, MailAuthenticationException.class, MailSendException.class})
	public ResponseEntity<ErrorResponse> handleMailExceptions(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse("message", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	

// Clase de respuesta de error

	public static class ErrorResponse {
		private String code;
		private String message;

		public ErrorResponse(String code, String message) {
			this.code = code;
			this.message = message;
		}

		// Getters y Setters

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	public static class ErrorResponseToken extends ErrorResponse {
		private final String token;

		public ErrorResponseToken(String code, String message, String token) {
			super(code, message);
			this.token = token;
		}

		public String getToken() {
			return token;
		}
	}

}
