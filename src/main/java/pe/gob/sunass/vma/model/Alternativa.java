package pe.gob.sunass.vma.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "vma", name = "alternativas")
public class Alternativa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alternativa")
    private Integer idAlternativa;

    @Column(name = "nombre_campo")
    private String nombreCampo;

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
    
    
    
    
}
