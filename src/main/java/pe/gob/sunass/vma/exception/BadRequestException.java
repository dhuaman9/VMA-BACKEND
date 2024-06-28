package pe.gob.sunass.vma.exception;

import javax.servlet.ServletException;

public class BadRequestException extends RuntimeException {//extends RuntimeException  ServletException 
    public BadRequestException(String message) {
        super(message);
    }
}
