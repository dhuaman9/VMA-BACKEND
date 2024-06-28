package pe.gob.sunass.vma.exception;

public class FailledValidationException extends RuntimeException {
  private static final long serialVersionUID = -4003221714287222703L;

  

  public FailledValidationException(String message) {
    super(message);
  }

  /*
  public FailledValidationException(String message, Throwable throwable) {
    super(message, throwable);
  }
  */
  
  
}
