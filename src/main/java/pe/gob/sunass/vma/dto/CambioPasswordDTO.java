package pe.gob.sunass.vma.dto;

public class CambioPasswordDTO {
	
	 private String passwordActual;
	 private String nuevaPassword;
	 private String confirmarPassword;
	 
	 
	public String getPasswordActual() {
		
		return passwordActual;
		
	}
	
	public void setPasswordActual(String passwordActual) {
		this.passwordActual = passwordActual;
	}
	public String getNuevaPassword() {
		return nuevaPassword;
	}
	public void setNuevaPassword(String nuevaPassword) {
		this.nuevaPassword = nuevaPassword;
	}
	public String getConfirmarPassword() {
		return confirmarPassword;
	}
	public void setConfirmarPassword(String confirmarPassword) {
		this.confirmarPassword = confirmarPassword;
	}
	
	 

}
