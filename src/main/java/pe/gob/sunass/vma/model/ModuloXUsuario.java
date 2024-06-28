package pe.gob.sunass.vma.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="modulo_x_usuario")
public class ModuloXUsuario {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_moduloxusuario")
	private Integer idModuloXUsuario;
	
	@ManyToOne
    @JoinColumn(name = "id_usuario",referencedColumnName="id", nullable=false)
    Usuario usuario;
	
	@ManyToOne
    @JoinColumn(name = "id_modulo",referencedColumnName="id_modulo", nullable=false)
    Modulo modulo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_at", nullable=false)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updated_at", nullable=true)
	private Date updatedAt;
	
	@Column(name = "id_usuario_registrar" )
	private Long idUsuarioRegistrar;
	
	@Column(name = "id_usuario_actualizacion")
	private Long idUsuarioActualizar;

	public Integer getIdModuloXUsuario() {
		return idModuloXUsuario;
	}

	public void setIdModuloXUsuario(Integer idModuloXUsuario) {
		this.idModuloXUsuario = idModuloXUsuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Modulo getModulo() {
		return modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
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

	public Long getIdUsuarioRegistrar() {
		return idUsuarioRegistrar;
	}

	public void setIdUsuarioRegistrar(Long idUsuarioRegistrar) {
		this.idUsuarioRegistrar = idUsuarioRegistrar;
	}

	public Long getIdUsuarioActualizar() {
		return idUsuarioActualizar;
	}

	public void setIdUsuarioActualizar(Long idUsuarioActualizar) {
		this.idUsuarioActualizar = idUsuarioActualizar;
	}


	
	
}
