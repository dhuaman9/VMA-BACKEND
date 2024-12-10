package pe.gob.sunass.vma.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.NoArgsConstructor;

//@Data
@NoArgsConstructor
@Entity
@Table(schema="vma", name="roles")
public class Role  implements Serializable {
	
	  private static final long serialVersionUID = -8301257302917630690L;

	  	@Id
		@GeneratedValue(strategy=GenerationType.IDENTITY)
	  	@Column(name="id_rol")
		private Integer idRol;

		@Column(name="nombre", length=30, nullable=false)
		private String nombre;

		@Temporal(TemporalType.TIMESTAMP)
		@Column(name="fecha_registro", nullable=false)
		private Date createdAt;

		@Column(name="auth", length=30)
	  	private String auth;

		@OneToMany(mappedBy="role", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
		@JsonIgnore
		private Set<Usuario> usuarios = new HashSet<Usuario>(0);

		

		public String getNombre() {
			return nombre;
		}
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public Date getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(Date createdAt) {
			this.createdAt = createdAt;
		}


		public Set<Usuario> getUsuarios() {
			return usuarios;
		}

		public void setUsuarios(Set<Usuario> usuarios) {
			this.usuarios = usuarios;
		}

		public String getAuth() {
			return auth;
		}

		public void setAuth(String auth) {
			this.auth = auth;
		}
		public Integer getIdRol() {
			return idRol;
		}
		public void setIdRol(Integer idRol) {
			this.idRol = idRol;
		}

	


}
