package pe.gob.sunass.vma.model.cuestionario;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(schema="vma", name="archivos")
public class Archivo  implements Serializable  {
	
	private static final long serialVersionUID = -8301257302917630690L;
	
	 	@Id
		@GeneratedValue(strategy=GenerationType.IDENTITY)
	 	@Column(name="id_archivo")
		private Integer idArchivo;//idArchivo

	    @Column(name="nombre_archivo", nullable=false)
		private String nombreArchivo;
	    
		@Column(name="id_alfresco")
		private String idAlfresco;
	
		@ManyToOne(fetch=FetchType.EAGER, optional=false)
		//@JoinColumn(name="id_registro_vma", referencedColumnName="id_registro_vma", nullable=true)
		@JoinColumn(name="id_registro_vma", referencedColumnName="id_registro_vma", nullable=false)
		private RegistroVMA registroVma;
		
		@Column(name="username", nullable=false)
		private String username;
		
		@Column(name="id_usuario_registro",  nullable=false)
		private Integer idUsuarioRegistro;
		
	

		@Temporal(TemporalType.TIMESTAMP)
		@Column(name="fecha_creacion", nullable=false)
		private Date createdAt;

		@Temporal(TemporalType.TIMESTAMP)
		@Column(name="fecha_actualizacion", nullable=true)
		private Date updatedAt;

		public Integer getIdArchivo() {
			return idArchivo;
		}

		public void setIdArchivo(Integer idArchivo) {
			this.idArchivo = idArchivo;
		}
		
		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
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

		public RegistroVMA getRegistroVma() {
			return registroVma;
		}

		public void setRegistroVma(RegistroVMA registroVma) {
			this.registroVma = registroVma;
		}

		public String getNombreArchivo() {
			return nombreArchivo;
		}

		public void setNombreArchivo(String nombreArchivo) {
			this.nombreArchivo = nombreArchivo;
		}

		public String getIdAlfresco() {
			return idAlfresco;
		}

		public void setIdAlfresco(String idAlfresco) {
			this.idAlfresco = idAlfresco;
		}

		public Integer getIdUsuarioRegistro() {
			return idUsuarioRegistro;
		}

		public void setIdUsuarioRegistro(Integer idUsuarioRegistro) {
			this.idUsuarioRegistro = idUsuarioRegistro;
		}
		
		

}
