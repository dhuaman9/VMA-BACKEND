package pe.gob.sunass.vma.exception;

public class PasswordNoCambiadoException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
    private final String token;

	public PasswordNoCambiadoException(String message, String token) {
        super(message);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
