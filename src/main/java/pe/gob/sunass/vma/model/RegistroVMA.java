package pe.gob.sunass.vma.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(schema="vma", name="registro_vma")
public class RegistroVMA implements Serializable {


	private static final long serialVersionUID = -8301257302917630690L;
	
    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_registro_vma")
	private Integer idRegistroVma;
    
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_empresa", referencedColumnName="id_empresa", nullable=false)
	private Empresa empresa;
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_ficha_registro", referencedColumnName="id_ficha_registro", nullable=false)
	private FichaRegistro fichaRegistro;
	
	@Column(name="estado")
	private String estado;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fecha_creacion", nullable=false)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fecha_actualizacion", nullable=true)
	private Date updatedAt;

	@Column(name="username", nullable=true)
	private String username;

	
	public Integer getIdRegistroVma() {
		return idRegistroVma;
	}

	public void setIdRegistroVma(Integer idRegistroVma) {
		this.idRegistroVma = idRegistroVma;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public FichaRegistro getFichaRegistro() {
		return fichaRegistro;
	}

	public void setFichaRegistro(FichaRegistro fichaRegistro) {
		this.fichaRegistro = fichaRegistro;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	
}
