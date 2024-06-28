package pe.gob.sunass.vma.exception;

public class ParamErrorException extends RuntimeException {
	 
    public ParamErrorException() {
    }
 
    public ParamErrorException(String message) {
        super(message);
    }
 
}