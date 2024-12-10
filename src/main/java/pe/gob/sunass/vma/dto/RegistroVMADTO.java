package pe.gob.sunass.vma.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class RegistroVMADTO implements Serializable {

	private static final long serialVersionUID = -4872705658825231581L;

	private Integer idRegistroVma;

	private EmpresaDTO empresa;

	private FichaDTO fichaRegistro;

	private String estado;

	@JsonProperty("createdAt")
	private String createdAt;

	@JsonProperty("updatedAt")
	private String updatedAt;

	private String username;

	public Integer getIdRegistroVma() {
		return idRegistroVma;
	}

	public void setIdRegistroVma(Integer idRegistroVma) {
		this.idRegistroVma = idRegistroVma;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public EmpresaDTO getEmpresa() {
		return empresa;
	}

	public void setEmpresa(EmpresaDTO empresa) {
		this.empresa = empresa;
	}

	public FichaDTO getFichaRegistro() {
		return fichaRegistro;
	}

	public void setFichaRegistro(FichaDTO fichaRegistro) {
		this.fichaRegistro = fichaRegistro;
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
