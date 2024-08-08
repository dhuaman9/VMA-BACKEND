package pe.gob.sunass.vma.dto;

public class AnexoTresListadoEPDTO {

		private String nombreEmpresa;
	    private String tamanioEmpresa;
	    private int nroUNDidentificados;
	    private int nroUNDinspeccionados;
	    private int nroUNDinscritos;
	    
	    
	    public AnexoTresListadoEPDTO() {
	    }

		public AnexoTresListadoEPDTO(String nombreEmpresa, String tamanioEmpresa, int nroUNDidentificados,
				int nroUNDinspeccionados, int nroUNDinscritos) {
			super();
			this.nombreEmpresa = nombreEmpresa;
			this.tamanioEmpresa = tamanioEmpresa;
			this.nroUNDidentificados = nroUNDidentificados;
			this.nroUNDinspeccionados = nroUNDinspeccionados;
			this.nroUNDinscritos = nroUNDinscritos;
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


		public int getNroUNDidentificados() {
			return nroUNDidentificados;
		}


		public void setNroUNDidentificados(int nroUNDidentificados) {
			this.nroUNDidentificados = nroUNDidentificados;
		}


		public int getNroUNDinspeccionados() {
			return nroUNDinspeccionados;
		}


		public void setNroUNDinspeccionados(int nroUNDinspeccionados) {
			this.nroUNDinspeccionados = nroUNDinspeccionados;
		}


		public int getNroUNDinscritos() {
			return nroUNDinscritos;
		}


		public void setNroUNDinscritos(int nroUNDinscritos) {
			this.nroUNDinscritos = nroUNDinscritos;
		}

	
	
}
