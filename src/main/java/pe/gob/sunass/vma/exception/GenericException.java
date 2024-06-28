package pe.gob.sunass.vma.exception;

public class GenericException extends Exception {
  private static final long serialVersionUID = -5956434192655222845L;

  private Integer status;

  public GenericException(Integer status, String message) {
    super(message);
    this.status = status;
  }

  public Integer getStatus() {
    return status;
  }
  public void setStatus(Integer status) {
    this.status = status;
  }
}
