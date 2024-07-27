package pe.gob.sunass.vma.dto;

public class AnexoRespuestaSiDTO {
    private String nombreEmpresa;
    private String tamanioEmpresa;
    private String respuesta;
    private int nroTrabajadores;

    public AnexoRespuestaSiDTO() {
    }

    public AnexoRespuestaSiDTO(String nombreEmpresa, String tamanioEmpresa, String respuesta, int nroTrabajadores) {
        this.nombreEmpresa = nombreEmpresa;
        this.tamanioEmpresa = tamanioEmpresa;
        this.respuesta = respuesta;
        this.nroTrabajadores = nroTrabajadores;
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

    public int getNroTrabajadores() {
        return nroTrabajadores;
    }

    public void setNroTrabajadores(int nroTrabajadores) {
        this.nroTrabajadores = nroTrabajadores;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
}
