package pe.gob.sunass.vma.model.cuestionario;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(schema = "vma", name = "preguntas")
public class Pregunta implements Serializable {

	private static final long serialVersionUID = -8301257302917630690L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_pregunta")
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

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_metadata", nullable = true)
	private Metadato metadato;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_pregunta", nullable = true)
	@OrderBy("idAlternativa ASC")
	private List<Alternativa> alternativas;

	@Column(name = "estado", nullable = false)
	private Boolean estado;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_registro", nullable = false)
	private Date createdAt;

//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name="fecha_actualizacion", nullable=true)
//	private Date updatedAt;

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

	public Metadato getMetadato() {
		return metadato;
	}

	public void setMetadato(Metadato metadato) {
		this.metadato = metadato;
	}
}