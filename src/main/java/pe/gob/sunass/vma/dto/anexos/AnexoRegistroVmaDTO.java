package pe.gob.sunass.vma.dto.anexos;

public class AnexoRegistroVmaDTO {
    private String nombreEmpresa;
    private String tamanioEmpresa;
    private boolean esRAT;
    private boolean epsRemitioInformacion;
    private boolean epsRemitioInforme;

    public AnexoRegistroVmaDTO(String nombreEmpresa, String tamanioEmpresa, boolean esRAT, boolean epsRemitioInformacion, boolean epsRemitioInforme) {
        this.nombreEmpresa = nombreEmpresa;
        this.tamanioEmpresa = tamanioEmpresa;
        this.esRAT = esRAT;
        this.epsRemitioInformacion = epsRemitioInformacion;
        this.epsRemitioInforme = epsRemitioInforme;
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

    public boolean isEsRAT() {
        return esRAT;
    }

    public void setEsRAT(boolean esRAT) {
        this.esRAT = esRAT;
    }

    public boolean isEpsRemitioInformacion() {
        return epsRemitioInformacion;
    }

    public void setEpsRemitioInformacion(boolean epsRemitioInformacion) {
        this.epsRemitioInformacion = epsRemitioInformacion;
    }

    public boolean isEpsRemitioInforme() {
        return epsRemitioInforme;
    }

    public void setEpsRemitioInforme(boolean epsRemitioInforme) {
        this.epsRemitioInforme = epsRemitioInforme;
    }
}
