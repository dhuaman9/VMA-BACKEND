package pe.gob.sunass.vma.dto;

public class RecuperarPasswordDTO {

	private final String nuevaPassword;
	private final String repetirPassword;

	public RecuperarPasswordDTO(String nuevaPassword, String repetirPassword) {
		this.nuevaPassword = nuevaPassword;
		this.repetirPassword = repetirPassword;
	}

	public String getNuevaPassword() {
		return nuevaPassword;
	}

	public String getRepetirPassword() {
		return repetirPassword;
	}
}
