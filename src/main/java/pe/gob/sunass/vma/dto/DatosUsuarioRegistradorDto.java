package pe.gob.sunass.vma.dto;

public class DatosUsuarioRegistradorDto {
    private String nombreCompleto;
    private String email;
    private String telefono;

    public DatosUsuarioRegistradorDto() {
    }

    public DatosUsuarioRegistradorDto(String nombreCompleto, String email, String telefono) {
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.telefono = telefono;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
