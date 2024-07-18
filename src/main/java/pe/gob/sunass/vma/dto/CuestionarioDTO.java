package pe.gob.sunass.vma.dto;

import java.util.List;

public class CuestionarioDTO {
    private Integer idCuestionario;
    private String nombre;
    private List<SeccionDTO> secciones;

    public CuestionarioDTO(Integer idCuestionario, String nombre, List<SeccionDTO> secciones) {
        this.idCuestionario = idCuestionario;
        this.nombre = nombre;
        this.secciones = secciones;
    }

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

    public List<SeccionDTO> getSecciones() {
        return secciones;
    }

    public void setSecciones(List<SeccionDTO> secciones) {
        this.secciones = secciones;
    }
}
