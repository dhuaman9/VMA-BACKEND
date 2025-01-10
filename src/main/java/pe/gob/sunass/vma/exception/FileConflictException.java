package pe.gob.sunass.vma.exception;


public class FileConflictException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileConflictException(String message) {
        super(message);
    }
}
