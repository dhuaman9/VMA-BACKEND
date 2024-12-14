package pe.gob.sunass.vma.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import pe.gob.sunass.vma.model.TipoEmpresa;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class EmpresaDTO {

	private Integer idEmpresa;

	private String nombre;

	private String regimen;

//	private String tipo;

	private TipoEmpresa tipoEmpresa;

	private Boolean estado;

	@JsonProperty("created_at")
	private String createdAt;

	@JsonProperty("updated_at")
	private String updatedAt;

	@JsonProperty("id_usuario_registro")
	private Integer idUsuarioRegistro;

	@JsonProperty("id_usuario_actualizacion")
	private Integer idUsuarioActualizacion;

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

	public TipoEmpresa getTipoEmpresa() {
		return tipoEmpresa;
	}

	public void setTipoEmpresa(TipoEmpresa tipoEmpresa) {
		this.tipoEmpresa = tipoEmpresa;
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

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
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
