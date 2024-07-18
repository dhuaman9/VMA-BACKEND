package pe.gob.sunass.vma.dto;

public class AlternativaDTO {
    private Integer idAlternativa;
    private String nombreCampo;
    private RespuestaDTO respuestaDTO;

    public AlternativaDTO(Integer idAlternativa, String nombreCampo, RespuestaDTO respuestaDTO) {
        this.idAlternativa = idAlternativa;
        this.nombreCampo = nombreCampo;
        this.respuestaDTO = respuestaDTO;
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

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }
}
