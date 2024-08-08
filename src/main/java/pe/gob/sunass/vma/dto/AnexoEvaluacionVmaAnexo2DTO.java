package pe.gob.sunass.vma.dto;

public class AnexoEvaluacionVmaAnexo2DTO {

	private String nombreEmpresa;
    private String tamanioEmpresa;
    private int nroMuestraInopinada;
    private int nroUNDSobrepasanParametrosAnexo2;
    private int nroUNDConPlazoAdicional;
    private int nroUNDSuscritoAcuerdo;
    
    
	public AnexoEvaluacionVmaAnexo2DTO() {
		
	}
	
	public AnexoEvaluacionVmaAnexo2DTO(String nombreEmpresa, String tamanioEmpresa, int nroMuestraInopinada,
			int nroUNDSobrepasanParametrosAnexo2, int nroUNDConPlazoAdicional, int nroUNDSuscritoAcuerdo) {
		super();
		this.nombreEmpresa = nombreEmpresa;
		this.tamanioEmpresa = tamanioEmpresa;
		this.nroMuestraInopinada = nroMuestraInopinada;
		this.nroUNDSobrepasanParametrosAnexo2 = nroUNDSobrepasanParametrosAnexo2;
		this.nroUNDConPlazoAdicional = nroUNDConPlazoAdicional;
		this.nroUNDSuscritoAcuerdo = nroUNDSuscritoAcuerdo;
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
	public int getNroMuestraInopinada() {
		return nroMuestraInopinada;
	}
	public void setNroMuestraInopinada(int nroMuestraInopinada) {
		this.nroMuestraInopinada = nroMuestraInopinada;
	}
	public int getNroUNDSobrepasanParametrosAnexo2() {
		return nroUNDSobrepasanParametrosAnexo2;
	}
	public void setNroUNDSobrepasanParametrosAnexo2(int nroUNDSobrepasanParametrosAnexo2) {
		this.nroUNDSobrepasanParametrosAnexo2 = nroUNDSobrepasanParametrosAnexo2;
	}
	public int getNroUNDConPlazoAdicional() {
		return nroUNDConPlazoAdicional;
	}
	public void setNroUNDConPlazoAdicional(int nroUNDConPlazoAdicional) {
		this.nroUNDConPlazoAdicional = nroUNDConPlazoAdicional;
	}
	public int getNroUNDSuscritoAcuerdo() {
		return nroUNDSuscritoAcuerdo;
	}
	public void setNroUNDSuscritoAcuerdo(int nroUNDSuscritoAcuerdo) {
		this.nroUNDSuscritoAcuerdo = nroUNDSuscritoAcuerdo;
	}
    
   
	
}
