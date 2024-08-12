package pe.gob.sunass.vma.dto;

public class AnexoIngresosImplVmaDTO {

	private String nombreEmpresa;
	private String tamanioEmpresa;
	private double ingresosVma;

	
	public AnexoIngresosImplVmaDTO() {

	}

	public AnexoIngresosImplVmaDTO(String nombreEmpresa, String tamanioEmpresa, double ingresosVma) {
		this.nombreEmpresa = nombreEmpresa;
		this.tamanioEmpresa = tamanioEmpresa;
		this.ingresosVma = ingresosVma;
	}

	public String getNombreEmpresa() {
		return nombreEmpresa;
	}

	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}

	public String getTamanioEmpresa() {
		return tamanioEmpresa;
	}

	public void setTamanioEmpresa(String tamanioEmpresa) {
		this.tamanioEmpresa = tamanioEmpresa;
	}

	public double getIngresosVma() {
		return ingresosVma;
	}

	public void setIngresosVma(int ingresosVma) {
		this.ingresosVma = ingresosVma;
	}

}
