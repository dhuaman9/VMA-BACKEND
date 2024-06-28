package pe.gob.sunass.vma.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;



@JsonInclude(Include.NON_EMPTY)
public class RoleDTO implements Serializable {
	  private static final long serialVersionUID = -8301257302917630690L;

		private Integer idRole;

		private String nombre;

		@JsonProperty("created_at")
		private String createdAt;

		@JsonProperty("updated_at")
		private String updatedAt;

		private String auth;

		private List<UsuarioDTO> usuarios = new ArrayList<UsuarioDTO>();

		
		public Integer getIdRole() {
			return idRole;
		}

		public void setIdRole(Integer idRole) {
			this.idRole = idRole;
		}

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public String getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(String createdAt) {
			this.createdAt = createdAt;
		}

		public String getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(String updatedAt) {
			this.updatedAt = updatedAt;
		}

		public String getAuth() {
			return auth;
		}

		public void setAuth(String auth) {
			this.auth = auth;
		}

		public List<UsuarioDTO> getUsuarios() {
			return usuarios;
		}

		public void setUsuarios(List<UsuarioDTO> usuarios) {
			this.usuarios = usuarios;
		}


		
		
		

	}

