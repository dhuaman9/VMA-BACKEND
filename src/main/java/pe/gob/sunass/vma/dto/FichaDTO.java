package pe.gob.sunass.vma.dto;

import java.time.LocalDate;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class FichaDTO {
	
	private static final long serialVersionUID = -4872705658825231581L;
	
	private Integer idFichaRegistro;
	
    private String anio;
	
    @JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate fechaInicio;
	
    @JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate fechaFin;
	
	@JsonProperty("created_at")
	private String createdAt;

	@JsonProperty("updated_at")
	private String updatedAt;
	
	@JsonProperty("id_usuario_registro")
	private Integer idUsuarioRegistro;
	
	@JsonProperty("id_usuario_actualizacion")
	private Integer idUsuarioActualizacion;

	

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
	
	
	
	

}
