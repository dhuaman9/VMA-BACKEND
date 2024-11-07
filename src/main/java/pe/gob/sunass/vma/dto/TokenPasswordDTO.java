package pe.gob.sunass.vma.dto;

import java.time.LocalDateTime;

public class TokenPasswordDTO {
    private final String token;
    private final boolean completo;
    private final LocalDateTime fechaExpiracion;

    public TokenPasswordDTO(String token, boolean completo, LocalDateTime fechaExpiracion) {
        this.token = token;
        this.completo = completo;
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getToken() {
        return token;
    }

    public boolean isCompleto() {
        return completo;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }
}
