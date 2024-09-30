package pe.gob.sunass.vma.dto.anexos;

import java.math.BigDecimal;

public class AnexoCostoTotalUNDDTO {

	private String nombreEmpresa;
    private String tamanioEmpresa;
    private BigDecimal costoTotalUND;
    private Integer nroUNDidentificados;
    private BigDecimal costoAnualPorUND;
    
    
	public AnexoCostoTotalUNDDTO() {
		
	}


	public AnexoCostoTotalUNDDTO(String nombreEmpresa, String tamanioEmpresa, BigDecimal costoTotalUND,
			Integer nroUNDidentificados, BigDecimal costoAnualPorUND) {
		super();
		this.nombreEmpresa = nombreEmpresa;
		this.tamanioEmpresa = tamanioEmpresa;
		this.costoTotalUND = costoTotalUND;
		this.nroUNDidentificados = nroUNDidentificados;
		this.costoAnualPorUND = costoAnualPorUND;
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


	public BigDecimal getCostoTotalUND() {
		return costoTotalUND;
	}


	public void setCostoTotalUND(BigDecimal costoTotalUND) {
		this.costoTotalUND = costoTotalUND;
	}


	public Integer getNroUNDidentificados() {
		return nroUNDidentificados;
	}


	public void setNroUNDidentificados(Integer nroUNDidentificados) {
		this.nroUNDidentificados = nroUNDidentificados;
	}


	public BigDecimal getCostoAnualPorUND() {
		return costoAnualPorUND;
	}


	public void setCostoAnualPorUND(BigDecimal costoAnualPorUND) {
		this.costoAnualPorUND = costoAnualPorUND;
	}


	
}
