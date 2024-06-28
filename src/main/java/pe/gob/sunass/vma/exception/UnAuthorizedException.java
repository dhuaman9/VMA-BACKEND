package pe.gob.sunass.vma.exception;

import javax.servlet.ServletException;

public class UnAuthorizedException extends ServletException{
	public UnAuthorizedException(String message) {
		super(message);
	}
}
