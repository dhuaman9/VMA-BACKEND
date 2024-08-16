package pe.gob.sunass.vma.model.cuestionario;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "vma", name = "alternativas")
public class Alternativa implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alternativa")
    private Integer idAlternativa;

    @Column(name = "nombre_campo")
    private String nombreCampo;

    @Column(name = "requerido")
	private Boolean requerido;
    
	public Integer getIdAlternativa() {
		return idAlternativa;
	}

	public void setIdAlternativa(Integer idAlternativa) {
		this.idAlternativa = idAlternativa;
	}

	public String getNombreCampo() {
		return nombreCampo;
	}

	public void setNombreCampo(String nombreCampo) {
		this.nombreCampo = nombreCampo;
	}

	public Boolean getRequerido() {
		return requerido;
	}

	public void setRequerido(Boolean requerido) {
		this.requerido = requerido;
	}
    
    
    
    
}
