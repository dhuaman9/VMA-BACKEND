package pe.gob.sunass.vma.dto.anexos;

import java.math.BigDecimal;

public class AnexoCostoTotalesIncurridosDTO {

	private String nombreEmpresa;
    private String tamanioEmpresa;
    private BigDecimal costoIdentInspeccionRegistroUND;
    private BigDecimal costoMuestrasInopinadas;
    private BigDecimal costosOtrosGastosImplementacion;
    
	public AnexoCostoTotalesIncurridosDTO() {
	
	}

	public AnexoCostoTotalesIncurridosDTO(String nombreEmpresa, String tamanioEmpresa,
			BigDecimal costoIdentInspeccionRegistroUND, BigDecimal costoMuestrasInopinadas,
			BigDecimal costosOtrosGastosImplementacion) {
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

	public BigDecimal getCostoIdentInspeccionRegistroUND() {
		return costoIdentInspeccionRegistroUND;
	}

	public void setCostoIdentInspeccionRegistroUND(BigDecimal costoIdentInspeccionRegistroUND) {
		this.costoIdentInspeccionRegistroUND = costoIdentInspeccionRegistroUND;
	}

	public BigDecimal getCostoMuestrasInopinadas() {
		return costoMuestrasInopinadas;
	}

	public void setCostoMuestrasInopinadas(BigDecimal costoMuestrasInopinadas) {
		this.costoMuestrasInopinadas = costoMuestrasInopinadas;
	}

	public BigDecimal getCostosOtrosGastosImplementacion() {
		return costosOtrosGastosImplementacion;
	}

	public void setCostosOtrosGastosImplementacion(BigDecimal costosOtrosGastosImplementacion) {
		this.costosOtrosGastosImplementacion = costosOtrosGastosImplementacion;
	}
    
	
	
	
	
}
