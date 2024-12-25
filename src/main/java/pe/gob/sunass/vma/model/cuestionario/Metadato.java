package pe.gob.sunass.vma.model.cuestionario;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(schema = "vma", name = "metadatos")
public class Metadato {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_metadata")
	private Integer id;

	@Column(name = "tipos_archivos_permitidos")
	@Type(type = "pe.gob.sunass.vma.model.EnumArrayFileType")
	@Enumerated(EnumType.STRING)
	private List<TipoArchivo> tipoArchivosPermitidos; // se usa para validar el tipo de archivo

	@Column(name = "max_size_in_mb")
	private Long maxSizeInMB; // se usa para validar el tamanio de archivos

	private boolean requerido;

	@Column(name = "is_decimal")
	private boolean isDecimal; // para valores monetarios, en los campos de  costos   vma

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<TipoArchivo> getTipoArchivosPermitidos() {
		return tipoArchivosPermitidos;
	}

	public void setTipoArchivosPermitidos(List<TipoArchivo> tipoArchivosPermitidos) {
		this.tipoArchivosPermitidos = tipoArchivosPermitidos;
	}

	public Long getMaxSizeInMB() {
		return maxSizeInMB;
	}

	public void setMaxSizeInMB(Long maxSizeInMB) {
		this.maxSizeInMB = maxSizeInMB;
	}

	public boolean isRequerido() {
		return requerido;
	}

	public void setRequerido(boolean requerido) {
		this.requerido = requerido;
	}

	public boolean isDecimal() {
		return isDecimal;
	}

	public void setDecimal(boolean decimal) {
		isDecimal = decimal;
	}
}