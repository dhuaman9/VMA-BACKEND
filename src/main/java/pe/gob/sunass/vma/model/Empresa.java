package pe.gob.sunass.vma.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(schema = "vma", name = "empresa")
public class Empresa implements Serializable {

	private static final long serialVersionUID = -8301257302917630690L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_empresa")
	private Integer idEmpresa;

	@Column(name = "nombre", length = 200, nullable = false)
	private String nombre;

	@Column(name = "regimen", length = 50, nullable = false)
	private String regimen;

//	@Column(name = "tipo", length = 50, nullable = false)
//	private String tipo;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_tipo_empresa", referencedColumnName="id_tipo_empresa", nullable=false)
	private TipoEmpresa tipoEmpresa;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_registro", nullable = false)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_actualizacion", nullable = true)
	private Date updatedAt;

	@Column(name = "id_usuario_registro", nullable = true)
	private Integer idUsuarioRegistro;

	@Column(name = "id_usuario_actualizacion", nullable = true)
	private Integer idUsuarioActualizacion;

//	@Column(name = "estado")
//	private Boolean estado;  //por el momento no se usara, segun DF: dificilmente se de de baja a una EPS en Peru.

	@OneToMany(mappedBy = "empresa")
	@JsonIgnore
	private List<RegistroVMA> registrosVMA;

	public String getRegimen() {
		return regimen;
	}

	public void setRegimen(String regimen) {
		this.regimen = regimen;
	}

//	public String getTipo() {
//		return tipo;
//	}
//
//	public void setTipo(String tipo) {
//		this.tipo = tipo;
//	}

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

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

//	public Boolean getEstado() {
//		return estado;
//	}
//
//	public void setEstado(Boolean estado) {
//		this.estado = estado;
//	}

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

	public TipoEmpresa getTipoEmpresa() {
		return tipoEmpresa;
	}

	public void setTipoEmpresa(TipoEmpresa tipoEmpresa) {
		this.tipoEmpresa = tipoEmpresa;
	}

}