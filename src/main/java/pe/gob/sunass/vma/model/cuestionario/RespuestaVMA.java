package pe.gob.sunass.vma.model.cuestionario;

import javax.persistence.*;

@Entity
@Table(name="respuesta_vma", schema = "vma")
public class RespuestaVMA {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id_respuesta_vma")
    private Integer idRespuestaVMA;

    @Column(name = "id_alternativa")
    private Integer idAlternativa;

    @Column(name = "id_pregunta")
    private Integer idPregunta;

    private String respuesta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_vma")
    private RegistroVMA registroVMA;

    public RespuestaVMA() {
    }

    public RespuestaVMA(Integer idRespuestaVMA, Integer idAlternativa, String respuesta, RegistroVMA registroVMA, Integer idPregunta) {
        this.idAlternativa = idAlternativa;
        this.respuesta = respuesta;
        this.registroVMA = registroVMA;
        this.idPregunta = idPregunta;
        this.idRespuestaVMA = idRespuestaVMA;
    }

    public Integer getIdRespuestaVMA() {
        return idRespuestaVMA;
    }

    public void setIdRespuestaVMA(Integer idRespuestaVMA) {
        this.idRespuestaVMA = idRespuestaVMA;
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

    public RegistroVMA getRegistroVMA() {
        return registroVMA;
    }

    public void setRegistroVMA(RegistroVMA registroVMA) {
        this.registroVMA = registroVMA;
    }

    public Integer getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(Integer idPregunta) {
        this.idPregunta = idPregunta;
    }
}
