package pe.gob.sunass.vma.dto;

import pe.gob.sunass.vma.model.TipoPregunta;

import java.util.List;

public class PreguntaDTO {
    private Integer idPregunta;
    private String descripcion;
    private Integer orden;
    private TipoPregunta tipoPregunta;
    private RespuestaDTO respuestaDTO;
    private List<AlternativaDTO> alternativas;

    public PreguntaDTO(Integer idPregunta, String descripcion, Integer orden, TipoPregunta tipoPregunta, List<AlternativaDTO> alternativas, RespuestaDTO respuestaDTO) {
        this.idPregunta = idPregunta;
        this.descripcion = descripcion;
        this.orden = orden;
        this.tipoPregunta = tipoPregunta;
        this.alternativas = alternativas;
        this.respuestaDTO = respuestaDTO;
    }

    public PreguntaDTO() {
    }

    public Integer getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(Integer idPregunta) {
        this.idPregunta = idPregunta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public TipoPregunta getTipoPregunta() {
        return tipoPregunta;
    }

    public void setTipoPregunta(TipoPregunta tipoPregunta) {
        this.tipoPregunta = tipoPregunta;
    }

    public List<AlternativaDTO> getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(List<AlternativaDTO> alternativas) {
        this.alternativas = alternativas;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }
}
