package pe.gob.sunass.vma.dto;

public class TokenPasswordDTO {
    private final String token;
    private final boolean completo;

    public TokenPasswordDTO(String token, boolean completo) {
        this.token = token;
        this.completo = completo;
    }

    public String getToken() {
        return token;
    }

    public boolean isCompleto() {
        return completo;
    }
}
