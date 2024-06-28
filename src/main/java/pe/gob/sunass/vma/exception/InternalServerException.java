package pe.gob.sunass.vma.exception;

import javax.servlet.ServletException;

public class InternalServerException extends ServletException {
    public InternalServerException(String message) {
      super(message);
    }
}