package pe.gob.sunass.vma.dto;

public class AlternativaDTO {
    private Integer idAlternativa;
    private String nombreCampo;
    private String respuesta;

    public AlternativaDTO(Integer idAlternativa, String nombreCampo, String respuesta) {
        this.idAlternativa = idAlternativa;
        this.nombreCampo = nombreCampo;
        this.respuesta = respuesta;
    }

    public Integer getIdAlternativa() {
        return idAlternativa;
    }

    public void setIdAlternativa(Integer idAlternativa) {
        this.idAlternativa = idAlternativa;
    }

    public String getNombreCampo() {
        return nombreCampo;
    }

    public void setNombreCampo(String nombreCampo) {
        this.nombreCampo = nombreCampo;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
}
