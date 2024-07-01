package pe.gob.sunass.vma.dto;

public class RespuestaDTO {
    private Integer idRespuesta;
    private Integer idAlternativa;
    private Integer idPregunta;
    private String respuesta;
    private Integer idRegistroVMA;

    public RespuestaDTO() {
    }

    public RespuestaDTO(Integer idRespuesta, Integer idAlternativa, Integer idPregunta, String respuesta) {
        this.idRespuesta = idRespuesta;
        this.idAlternativa = idAlternativa;
        this.idPregunta = idPregunta;
        this.respuesta = respuesta;
    }

    public RespuestaDTO(Integer idAlternativa, String respuesta, Integer idRegistroVMA) {
        this.idAlternativa = idAlternativa;
        this.respuesta = respuesta;
        this.idRegistroVMA = idRegistroVMA;
    }

    public Integer getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(Integer idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public Integer getIdAlternativa() {
        return idAlternativa;
    }

    public void setIdAlternativa(Integer idAlternativa) {
        this.idAlternativa = idAlternativa;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public Integer getIdRegistroVMA() {
        return idRegistroVMA;
    }

    public void setIdRegistroVMA(Integer idRegistroVMA) {
        this.idRegistroVMA = idRegistroVMA;
    }

    public Integer getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(Integer idPregunta) {
        this.idPregunta = idPregunta;
    }
}
