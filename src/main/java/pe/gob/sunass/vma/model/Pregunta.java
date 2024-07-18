package pe.gob.sunass.vma.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(schema="vma", name="preguntas")
public class Pregunta  implements Serializable{
	
	private static final long serialVersionUID = -8301257302917630690L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_pregunta")
	private Integer idPregunta;

	@Column(name = "descripcion")
    private String descripcion;
	
	@Column(name = "orden")
    private Integer orden;

	@Column(name = "requerido")
	private Boolean requerido;

	@Column(name = "tipo_pregunta")
	@Enumerated(EnumType.STRING)
	private TipoPregunta tipoPregunta;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_pregunta", nullable = true)
	private List<Alternativa> alternativas;
	
	@Column(name="estado",  nullable=false)
	private Boolean estado;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fecha_registro", nullable=false)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fecha_actualizacion", nullable=true)
	private Date updatedAt;

	@Column(name="id_usuario_registro", nullable=true)
	private Integer idUsuarioRegistro;
	
	@Column(name="id_usuario_actualizacion", nullable=true)
	private Integer idUsuarioActualizacion;

	@OneToOne
	@JoinColumn(name = "pregunta_dependiente_id")
	private Pregunta preguntaDependiente;

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

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
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

	public Boolean getRequerido() {
		return requerido;
	}

	public void setRequerido(Boolean requerido) {
		this.requerido = requerido;
	}

	public TipoPregunta getTipoPregunta() {
		return tipoPregunta;
	}

	public void setTipoPregunta(TipoPregunta tipoPregunta) {
		this.tipoPregunta = tipoPregunta;
	}

	public List<Alternativa> getAlternativas() {
		return alternativas;
	}

	public void setAlternativas(List<Alternativa> alternativas) {
		this.alternativas = alternativas;
	}

	public Pregunta getPreguntaDependiente() {
		return preguntaDependiente;
	}

	public void setPreguntaDependiente(Pregunta preguntaDependiente) {
		this.preguntaDependiente = preguntaDependiente;
	}
}
