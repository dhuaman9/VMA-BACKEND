package pe.gob.sunass.vma.model.cuestionario;

import java.util.Date;

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
    
    @Column(name="id_usuario_registro", nullable=true)
	private Integer idUsuarioRegistro;
	
	@Column(name="id_usuario_actualizacion", nullable=true)
	private Integer idUsuarioActualizacion;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fecha_registro", nullable=false)
	private Date fechaRegistro;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fecha_actualizacion", nullable=true)
	private Date fechaActualizacion;

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

	public Integer getIdUsuarioRegistro() {
		return idUsuarioRegistro;
	}

	public void setIdUsuarioRegistro(Integer idUsuarioRegistro) {
		this.idUsuarioRegistro = idUsuarioRegistro;
	}

	public Integer getIdUsuarioActualizacion() {
		return idUsuarioActualizacion;
	}

	public void setIdUsuarioActualizacion(Integer idUsuarioActualizacion) {
		this.idUsuarioActualizacion = idUsuarioActualizacion;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(Date fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
    
    
    
    
}
