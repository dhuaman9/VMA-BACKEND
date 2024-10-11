package pe.gob.sunass.vma.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;



@Entity
@Table(schema="vma", name="ficha_registro")
public class FichaRegistro implements Serializable {
	
	private static final long serialVersionUID = -8301257302917630690L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_ficha_registro")
	private Integer idFichaRegistro;

	@Column(name = "anio")
    private String anio;
	
	 @JsonFormat(pattern = "dd/MM/yyyy")
	@Column(name="fecha_inicio")
	private LocalDate fechaInicio;
	
	 @JsonFormat(pattern = "dd/MM/yyyy")
	@Column(name="fecha_fin")
	private LocalDate fechaFin;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_at", nullable=false)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updated_at", nullable=true)
	private Date updatedAt;

	@Column(name="id_usuario_registro", nullable=true)
	private Integer idUsuarioRegistro;
	
	@Column(name="id_usuario_actualizacion", nullable=true)
	private Integer idUsuarioActualizacion;
	
	@OneToMany(mappedBy = "fichaRegistro")
	@JsonIgnore 
	private List<RegistroVMA> registrosVMA;
	
	

	public Integer getIdFichaRegistro() {
		return idFichaRegistro;
	}

	public void setIdFichaRegistro(Integer idFichaRegistro) {
		this.idFichaRegistro = idFichaRegistro;
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
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

	public List<RegistroVMA> getRegistrosVMA() {
		return registrosVMA;
	}

	public void setRegistrosVMA(List<RegistroVMA> registrosVMA) {
		this.registrosVMA = registrosVMA;
	}

	
}
