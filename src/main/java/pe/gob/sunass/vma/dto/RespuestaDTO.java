package pe.gob.sunass.vma.dto;

public class RespuestaDTO {
    private Integer idAlternativa;
    private Integer idPregunta;
    private String respuesta;
    private Integer idRegistroVMA;

    public RespuestaDTO(Integer idAlternativa, String respuesta, Integer idRegistroVMA) {
        this.idAlternativa = idAlternativa;
        this.respuesta = respuesta;
        this.idRegistroVMA = idRegistroVMA;
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
