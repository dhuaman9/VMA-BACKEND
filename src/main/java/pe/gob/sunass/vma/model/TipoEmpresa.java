package pe.gob.sunass.vma.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(schema="vma", name="tipo_empresa")
public class TipoEmpresa implements Serializable  {
	
	private static final long serialVersionUID = -8301257302917630690L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_tipo_empresa")
	private Integer idTipoEmpresa;

	@Column(name="nombre", length=60, nullable=false)
	private String nombre;
	
	@Column(name="descripcion",  nullable=true)
	private String descripcion;
	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fecha_creacion", nullable=false)
	private Date createdAt;


	public Integer getIdTipoEmpresa() {
		return idTipoEmpresa;
	}


	public void setIdTipoEmpresa(Integer idTipoEmpresa) {
		this.idTipoEmpresa = idTipoEmpresa;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public Date getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	

}
