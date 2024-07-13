package pe.gob.sunass.vma.dto;


import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_EMPTY)
public class ArchivoDTO implements Serializable {

	 	private static final long serialVersionUID = 2878697413683046647L;

		private Integer idArchivo;

		private RegistroVMADTO registroVMADTO;

		@JsonProperty("nombreArchivo")
		private String nombreArchivo;

		@JsonProperty("idAlfresco")
		private String idAlfresco;

		@JsonProperty("fechaCreacion")
		private String createdAt;

		@JsonProperty("fechaActualizacion")
		private String updatedAt;

		
		public Integer getIdArchivo() {
			return idArchivo;
		}

		public void setIdArchivo(Integer idArchivo) {
			this.idArchivo = idArchivo;
		}

		public RegistroVMADTO getRegistroVMADTO() {
			return registroVMADTO;
		}

		public void setRegistroVMADTO(RegistroVMADTO registroVMADTO) {
			this.registroVMADTO = registroVMADTO;
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


}
