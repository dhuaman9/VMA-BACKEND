package pe.gob.sunass.vma.dto;

public class AnexoCostoTotalMuestrasInopinadasDTO {
	
	private String nombreEmpresa;
    private String tamanioEmpresa;
    private double costoPorTomaMuestrasInopinadas;
    private int nroMuestrasInopinadas;
    private double costoAnualMuestraInopinada;
    
    
	public AnexoCostoTotalMuestrasInopinadasDTO() {
		
	}
	
	public AnexoCostoTotalMuestrasInopinadasDTO(String nombreEmpresa, String tamanioEmpresa,
			double costoPorTomaMuestrasInopinadas, int nroMuestrasInopinadas, double costoAnualMuestraInopinada) {
		
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
	public double getCostoPorTomaMuestrasInopinadas() {
		return costoPorTomaMuestrasInopinadas;
	}
	public void setCostoPorTomaMuestrasInopinadas(double costoPorTomaMuestrasInopinadas) {
		this.costoPorTomaMuestrasInopinadas = costoPorTomaMuestrasInopinadas;
	}
	public double getNroMuestrasInopinadas() {
		return nroMuestrasInopinadas;
	}
	public void setNroMuestrasInopinadas(int nroMuestrasInopinadas) {
		this.nroMuestrasInopinadas = nroMuestrasInopinadas;
	}
	public double getCostoAnualMuestraInopinada() {
		return costoAnualMuestraInopinada;
	}
	public void setCostoAnualMuestraInopinada(double costoAnualMuestraInopinada) {
		this.costoAnualMuestraInopinada = costoAnualMuestraInopinada;
	}
    
	
	

}
