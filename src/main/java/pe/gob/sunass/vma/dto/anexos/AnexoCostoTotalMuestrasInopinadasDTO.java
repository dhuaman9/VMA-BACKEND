package pe.gob.sunass.vma.dto.anexos;

import java.math.BigDecimal;

public class AnexoCostoTotalMuestrasInopinadasDTO {
	
	private String nombreEmpresa;
    private String tamanioEmpresa;
    private  BigDecimal costoPorTomaMuestrasInopinadas;
    private int nroMuestrasInopinadas;
    private  BigDecimal costoAnualMuestraInopinada;
    
    
	public AnexoCostoTotalMuestrasInopinadasDTO() {
		
	}
	
	public AnexoCostoTotalMuestrasInopinadasDTO(String nombreEmpresa, String tamanioEmpresa,
			BigDecimal costoPorTomaMuestrasInopinadas, int nroMuestrasInopinadas, BigDecimal costoAnualMuestraInopinada) {
		
		this.nombreEmpresa = nombreEmpresa;
		this.tamanioEmpresa = tamanioEmpresa;
		this.costoPorTomaMuestrasInopinadas = costoPorTomaMuestrasInopinadas;
		this.nroMuestrasInopinadas = nroMuestrasInopinadas;
		this.costoAnualMuestraInopinada = costoAnualMuestraInopinada;
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
	public BigDecimal getCostoPorTomaMuestrasInopinadas() {
		return costoPorTomaMuestrasInopinadas;
	}
	public void setCostoPorTomaMuestrasInopinadas(BigDecimal costoPorTomaMuestrasInopinadas) {
		this.costoPorTomaMuestrasInopinadas = costoPorTomaMuestrasInopinadas;
	}
	public double getNroMuestrasInopinadas() {
		return nroMuestrasInopinadas;
	}
	public void setNroMuestrasInopinadas(int nroMuestrasInopinadas) {
		this.nroMuestrasInopinadas = nroMuestrasInopinadas;
	}
	public BigDecimal getCostoAnualMuestraInopinada() {
		return costoAnualMuestraInopinada;
	}
	public void setCostoAnualMuestraInopinada(BigDecimal costoAnualMuestraInopinada) {
		this.costoAnualMuestraInopinada = costoAnualMuestraInopinada;
	}
    
	
	

}
