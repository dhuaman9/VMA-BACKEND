package pe.gob.sunass.vma.dto;

public class AnexoCostoTotalUNDDTO {

	private String nombreEmpresa;
    private String tamanioEmpresa;
    private double costoTotalUND;
    private double nroUNDidentificados;
    private double porcentajeCostoAnualUND;
    
    
	public AnexoCostoTotalUNDDTO() {
		
	}


	public AnexoCostoTotalUNDDTO(String nombreEmpresa, String tamanioEmpresa, double costoTotalUND,
			double nroUNDidentificados, double porcentajeCostoAnualUND) {
		super();
		this.nombreEmpresa = nombreEmpresa;
		this.tamanioEmpresa = tamanioEmpresa;
		this.costoTotalUND = costoTotalUND;
		this.nroUNDidentificados = nroUNDidentificados;
		this.porcentajeCostoAnualUND = porcentajeCostoAnualUND;
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


	public double getCostoTotalUND() {
		return costoTotalUND;
	}


	public void setCostoTotalUND(double costoTotalUND) {
		this.costoTotalUND = costoTotalUND;
	}


	public double getNroUNDidentificados() {
		return nroUNDidentificados;
	}


	public void setNroUNDidentificados(double nroUNDidentificados) {
		this.nroUNDidentificados = nroUNDidentificados;
	}


	public double getPorcentajeCostoAnualUND() {
		return porcentajeCostoAnualUND;
	}


	public void setPorcentajeCostoAnualUND(double porcentajeCostoAnualUND) {
		this.porcentajeCostoAnualUND = porcentajeCostoAnualUND;
	}
	
	
	
	
	
	
	
}
