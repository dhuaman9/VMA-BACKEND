package pe.gob.sunass.vma.dto;

public class AnexoReclamosVMADTO {
	
	private String nombreEmpresa;
    private String tamanioEmpresa;
    private int nroUNDInscritos;
    private int nroReclamosRecibidos;
    private int nroReclamosFundados;
    
    
	public AnexoReclamosVMADTO() {
		
	}
	public AnexoReclamosVMADTO(String nombreEmpresa, String tamanioEmpresa, int nroUNDInscritos, int nroReclamosRecibidos,
			int nroReclamosFundados) {
		super();
		this.nombreEmpresa = nombreEmpresa;
		this.tamanioEmpresa = tamanioEmpresa;
		this.nroUNDInscritos = nroUNDInscritos;
		this.nroReclamosRecibidos = nroReclamosRecibidos;
		this.nroReclamosFundados = nroReclamosFundados;
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
	public int getNroUNDInscritos() {
		return nroUNDInscritos;
	}
	public void setNroUNDInscritos(int nroUNDInscritos) {
		this.nroUNDInscritos = nroUNDInscritos;
	}
	public int getNroReclamosRecibidos() {
		return nroReclamosRecibidos;
	}
	public void setNroReclamosRecibidos(int nroReclamosRecibidos) {
		this.nroReclamosRecibidos = nroReclamosRecibidos;
	}
	public int getNroReclamosFundados() {
		return nroReclamosFundados;
	}
	public void setNroReclamosFundados(int nroReclamosFundados) {
		this.nroReclamosFundados = nroReclamosFundados;
	}
    
    
    
    
    

}
