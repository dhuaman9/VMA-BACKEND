package pe.gob.sunass.vma.exception;



public class BadRequestException extends RuntimeException {//extends RuntimeException  ServletException 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadRequestException(String message) {
        super(message);
    }
}
