package pe.gob.sunass.vma.exception;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestExceptionHandler(BadRequestException ex) {
       // this._logger.error("ERROR", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El cuerpo del parámetro no puede estar vacío");
    }
	
    //para validar datos y enviar mensaje de error al front.
    @ExceptionHandler(FailledValidationException.class)
    public ResponseEntity<?> handleValidationException(FailledValidationException ex) {
    	logger.info("BAD REQUEST : FailledValidationException"+ ex.getMessage());
    	 ErrorResponse errorResponse = new ErrorResponse("BAD_REQUEST", ex.getMessage());
	     return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
      
    }
    
    @ExceptionHandler(FileConflictException.class)
    public ResponseEntity<?> handleFileConflictException(FileConflictException ex) {
    	logger.info("CONFLICT EXCEPTION : FileConflictException"+ ex.getMessage());
    	 ErrorResponse errorResponse = new ErrorResponse("CONFLICT", ex.getMessage());
	     return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
      
    }

    @ExceptionHandler(PasswordNoCambiadoException.class)
    public ResponseEntity<?> handleFileConflictException(PasswordNoCambiadoException ex) {
        logger.info("PASSWORD_NO_CAMBIADO_EXCEPTION : PasswordNoCambiadoException"+ ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponseToken("PASSWORD_NO_CAMBIADO_EXCEPTION", ex.getMessage(), ex.getToken());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

    }
    
    
	@ExceptionHandler(LocalNotFoundException.class)
    public ResponseEntity<?> localNotFoundException(LocalNotFoundException exception){
		
		Map<String, Object> customError =new HashMap<>();
		customError.put("message", exception.getMessage());
		
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(customError);
    }
	
	/*
	@ExceptionHandler(value = {ExpiredJwtException.class})
	 public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
	  String requestUri = ((ServletWebRequest)request).getRequest().getRequestURI().toString();
	  ExceptionMessage exceptionMessage = new ExceptionMessage(ex.getMessage(), requestUri);
	  return new ResponseEntity<>(exceptionMessage, new HttpHeaders(), HttpStatus.FORBIDDEN);
	 }
	*/
	
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
