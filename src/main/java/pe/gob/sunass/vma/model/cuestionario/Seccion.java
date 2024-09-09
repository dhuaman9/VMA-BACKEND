package pe.gob.sunass.vma.model.cuestionario;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(schema="vma", name="secciones")
public class Seccion implements Serializable{
	
	
	private static final long serialVersionUID = -8301257302917630690L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_seccion")
	private Integer idSeccion;

	@Column(name = "nombre")
    private String nombre;
	
	@Column(name = "orden")
    private Integer orden;
	
	@Column(name="estado",  nullable=false)
	private Boolean estado;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fecha_creacion", nullable=false)
	private Date createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cuestionario")
	@JsonIgnore
	private Cuestionario cuestionario;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_seccion", nullable = true)
	@OrderBy("idPregunta ASC")
	private List<Pregunta> preguntas;

	public Integer getIdSeccion() {
		return idSeccion;
	}

	public void setIdSeccion(Integer idSeccion) {
		this.idSeccion = idSeccion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
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

	
	public List<Pregunta> getPreguntas() {
		return preguntas;
	}

	public void setPreguntas(List<Pregunta> preguntas) {
		this.preguntas = preguntas;
	}

	public Cuestionario getCuestionario() {
		return cuestionario;
	}

	public void setCuestionario(Cuestionario cuestionario) {
		this.cuestionario = cuestionario;
	}
}
