package pe.gob.sunass.vma.dto.anexos;

public class AnexoEvaluacionVmaAnexo1DTO {
	
	private String nombreEmpresa;
    private String tamanioEmpresa;
    private int nroMuestraInopinada;
    private int nroUNDSobrepasanParametrosAnexo1;
    private int nroUNDFacturadosPorAnexo1;
    private int nroUNDRealizaronPagoPorAnexo1;
    
	public AnexoEvaluacionVmaAnexo1DTO() {
		
	}

	
	public AnexoEvaluacionVmaAnexo1DTO(String nombreEmpresa, String tamanioEmpresa, int nroMuestraInopinada,
			int nroUNDSobrepasanParametrosAnexo1, int nroUNDFacturadosPorAnexo1, int nroUNDRealizaronPagoPorAnexo1) {
		super();
		this.nombreEmpresa = nombreEmpresa;
		this.tamanioEmpresa = tamanioEmpresa;
		this.nroMuestraInopinada = nroMuestraInopinada;
		this.nroUNDSobrepasanParametrosAnexo1 = nroUNDSobrepasanParametrosAnexo1;
		this.nroUNDFacturadosPorAnexo1 = nroUNDFacturadosPorAnexo1;
		this.nroUNDRealizaronPagoPorAnexo1 = nroUNDRealizaronPagoPorAnexo1;
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

	public int getNroUNDSobrepasanParametrosAnexo1() {
		return nroUNDSobrepasanParametrosAnexo1;
	}

	public void setNroUNDSobrepasanParametrosAnexo1(int nroUNDSobrepasanParametrosAnexo1) {
		this.nroUNDSobrepasanParametrosAnexo1 = nroUNDSobrepasanParametrosAnexo1;
	}

	public int getNroUNDFacturadosPorAnexo1() {
		return nroUNDFacturadosPorAnexo1;
	}

	public void setNroUNDFacturadosPorAnexo1(int nroUNDFacturadosPorAnexo1) {
		this.nroUNDFacturadosPorAnexo1 = nroUNDFacturadosPorAnexo1;
	}

	public int getNroUNDRealizaronPagoPorAnexo1() {
		return nroUNDRealizaronPagoPorAnexo1;
	}

	public void setNroUNDRealizaronPagoPorAnexo1(int nroUNDRealizaronPagoPorAnexo1) {
		this.nroUNDRealizaronPagoPorAnexo1 = nroUNDRealizaronPagoPorAnexo1;
	}

    
    
    
    
}
