package pe.gob.sunass.vma.dto;

public class AnexoCostoTotalesIncurridosDTO {

	private String nombreEmpresa;
    private String tamanioEmpresa;
    private double costoIdentInspeccionRegistroUND;
    private double costoMuestrasInopinadas;
    private double costosOtrosGastosImplementacion;
    
	public AnexoCostoTotalesIncurridosDTO() {
	
	}

	public AnexoCostoTotalesIncurridosDTO(String nombreEmpresa, String tamanioEmpresa,
			double costoIdentInspeccionRegistroUND, double costoMuestrasInopinadas,
			double costosOtrosGastosImplementacion) {
		super();
		this.nombreEmpresa = nombreEmpresa;
		this.tamanioEmpresa = tamanioEmpresa;
		this.costoIdentInspeccionRegistroUND = costoIdentInspeccionRegistroUND;
		this.costoMuestrasInopinadas = costoMuestrasInopinadas;
		this.costosOtrosGastosImplementacion = costosOtrosGastosImplementacion;
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

	public double getCostoIdentInspeccionRegistroUND() {
		return costoIdentInspeccionRegistroUND;
	}

	public void setCostoIdentInspeccionRegistroUND(double costoIdentInspeccionRegistroUND) {
		this.costoIdentInspeccionRegistroUND = costoIdentInspeccionRegistroUND;
	}

	public double getCostoMuestrasInopinadas() {
		return costoMuestrasInopinadas;
	}

	public void setCostoMuestrasInopinadas(double costoMuestrasInopinadas) {
		this.costoMuestrasInopinadas = costoMuestrasInopinadas;
	}

	public double getCostosOtrosGastosImplementacion() {
		return costosOtrosGastosImplementacion;
	}

	public void setCostosOtrosGastosImplementacion(double costosOtrosGastosImplementacion) {
		this.costosOtrosGastosImplementacion = costosOtrosGastosImplementacion;
	}
    
	
	
	
	
}
