package pe.gob.sunass.vma.model.cuestionario;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(schema = "vma", name = "cuestionarios")
public class Cuestionario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuestionario")
    private Integer idCuestionario;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "estado")
    private Boolean estado;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fecha_creacion", nullable=false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fecha_actualizacion", nullable=true)
    private Date updatedAt;

    @Transient
    private List<Seccion> secciones;

	public Integer getIdCuestionario() {
		return idCuestionario;
	}

	public void setIdCuestionario(Integer idCuestionario) {
		this.idCuestionario = idCuestionario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
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

	public List<Seccion> getSecciones() {
		return secciones;
	}

	public void setSecciones(List<Seccion> secciones) {
		this.secciones = secciones;
	}
    
    
    
    
    
    
}
