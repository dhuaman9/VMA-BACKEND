package pe.gob.sunass.vma.dto.anexos;

import java.math.BigDecimal;

public class AnexoIngresosImplVmaDTO {

	private String nombreEmpresa;
	private String tamanioEmpresa;
	private  BigDecimal ingresosVma;

	
	public AnexoIngresosImplVmaDTO() {

	}

	public AnexoIngresosImplVmaDTO(String nombreEmpresa, String tamanioEmpresa, BigDecimal ingresosVma) {
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

	public BigDecimal getIngresosVma() {
		return ingresosVma;
	}

	public void setIngresosVma(BigDecimal ingresosVma) {
		this.ingresosVma = ingresosVma;
	}

}
