package pe.gob.sunass.vma.dto.anexos;

public class AnexoPorcentajeMuestraInopinadaDTO {

	private String nombreEmpresa;
    private String tamanioEmpresa;
   // private int nroUNDidentificados;
    private int nroUNDinscritos;
    private int nroMuestraInopinada;
    private double porcentajeMuestraInopinada;
    
    public AnexoPorcentajeMuestraInopinadaDTO() {
		
	}
    
	public AnexoPorcentajeMuestraInopinadaDTO(String nombreEmpresa, String tamanioEmpresa,
			int nroUNDinscritos, int nroMuestraInopinada, double porcentajeMuestraInopinada) {
	
		this.nombreEmpresa = nombreEmpresa;
		this.tamanioEmpresa = tamanioEmpresa;
		this.nroUNDinscritos = nroUNDinscritos;
		this.nroMuestraInopinada = nroMuestraInopinada;
		this.porcentajeMuestraInopinada = porcentajeMuestraInopinada;
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



	public int getNroUNDinscritos() {
		return nroUNDinscritos;
	}

	public void setNroUNDinscritos(int nroUNDinscritos) {
		this.nroUNDinscritos = nroUNDinscritos;
	}

	public int getNroMuestraInopinada() {
		return nroMuestraInopinada;
	}

	public void setNroMuestraInopinada(int nroMuestraInopinada) {
		this.nroMuestraInopinada = nroMuestraInopinada;
	}

	public double getPorcentajeMuestraInopinada() {
		return porcentajeMuestraInopinada;
	}

	public void setPorcentajeMuestraInopinada(double porcentajeMuestraInopinada) {
		this.porcentajeMuestraInopinada = porcentajeMuestraInopinada;
	}
    
    
}
