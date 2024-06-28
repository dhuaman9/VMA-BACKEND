package pe.gob.sunass.vma.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="modulo")
public class Modulo {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_modulo")
	private Long idModulo;
	
	@Column(name="descripcion")
	private String descripcion;
	
	@OneToMany(mappedBy = "usuario",fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    Set<ModuloXUsuario> moduloXUsuario;

	public Long getIdModulo() {
		return idModulo;
	}

	public void setIdModulo(Long idModulo) {
		this.idModulo = idModulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Set<ModuloXUsuario> getModuloXUsuario() {
		return moduloXUsuario;
	}

	public void setModuloXUsuario(Set<ModuloXUsuario> moduloXUsuario) {
		this.moduloXUsuario = moduloXUsuario;
	}

	
	
	

}
