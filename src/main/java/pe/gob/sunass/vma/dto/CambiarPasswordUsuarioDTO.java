package pe.gob.sunass.vma.dto;

public class CambiarPasswordUsuarioDTO extends RecuperarPasswordDTO {
    private final String username;

    public CambiarPasswordUsuarioDTO(String username, String nuevaPassword, String repetirPassword) {
        super(nuevaPassword, repetirPassword);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
