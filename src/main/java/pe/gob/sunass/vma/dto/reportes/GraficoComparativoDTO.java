package pe.gob.sunass.vma.dto.reportes;

public class GraficoComparativoDTO {

	//	private String nombreEmpresa;
	    private String tipoEmpresa;
	    private int UNDregistrados;
	    private int UNDinspeccionados;
	    private int UNDidentificados;
	    private double  porcentajeUNDinspeccionados;
	    private double  porcentajeUNDidentificados;
	  
	    public GraficoComparativoDTO() {
			
		}
	    
		public GraficoComparativoDTO(String tipoEmpresa, int UNDregistrados, int UNDinspeccionados,
				int UNDidentificados, double porcentajeUNDinspeccionados, double porcentajeUNDidentificados) {// total
			this.tipoEmpresa = tipoEmpresa;
			this.UNDregistrados = UNDregistrados;
			this.UNDinspeccionados = UNDinspeccionados;
			this.UNDidentificados = UNDidentificados;
			this.porcentajeUNDinspeccionados = porcentajeUNDinspeccionados;
			this.porcentajeUNDidentificados = porcentajeUNDidentificados;
			//this.total = total;
		}
		
		
		public String getTipoEmpresa() {
			return tipoEmpresa;
		}
		public void setTipoEmpresa(String tipoEmpresa) {
			this.tipoEmpresa = tipoEmpresa;
		}
		public int getUNDregistrados() {
			return UNDregistrados;
		}
		public void setUNDregistrados(int uNDregistrados) {
			UNDregistrados = uNDregistrados;
		}
		public int getUNDinspeccionados() {
			return UNDinspeccionados;
		}
		public void setUNDinspeccionados(int uNDinspeccionados) {
			UNDinspeccionados = uNDinspeccionados;
		}
		public int getUNDidentificados() {
			return UNDidentificados;
		}
		public void setUNDidentificados(int uNDidentificados) {
			UNDidentificados = uNDidentificados;
		}
		public double getPorcentajeUNDinspeccionados() {
			return porcentajeUNDinspeccionados;
		}
		public void setPorcentajeUNDinspeccionados(int porcentajeUNDinspeccionados) {
			this.porcentajeUNDinspeccionados = porcentajeUNDinspeccionados;
		}
		public double getPorcentajeUNDidentificados() {
			return porcentajeUNDidentificados;
		}
		public void setPorcentajeUNDidentificados(int porcentajeUNDidentificados) {
			this.porcentajeUNDidentificados = porcentajeUNDidentificados;
		}

		 
	
}
