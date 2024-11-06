package pe.gob.sunass.vma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.sunass.vma.exception.ResourceNotFoundException;
import pe.gob.sunass.vma.model.TokenPassword;
import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.repository.TokenPasswordRepository;

import java.util.UUID;

@Service
public class TokenPasswordService {
    private final TokenPasswordRepository tokenPasswordRepository;

    @Autowired
    public TokenPasswordService(TokenPasswordRepository tokenPasswordRepository) {
        this.tokenPasswordRepository = tokenPasswordRepository;
    }

    public String crearToken(Usuario usuario) {
        TokenPassword tokenPassword = new TokenPassword();
        tokenPassword.setToken(generarToken());
        tokenPassword.setFechaExpiracion(null);
        tokenPassword.setUsuario(usuario);
        tokenPassword.setCompletado(false);
        tokenPasswordRepository.save(tokenPassword);
        return tokenPassword.getToken();
    }

    public TokenPassword findTokenByToken(String token) {
        return tokenPasswordRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("Token no encontrado"));
    }

    public void save(TokenPassword tokenPassword) {
        tokenPasswordRepository.save(tokenPassword);
    }

    private String generarToken() {
        return UUID.randomUUID().toString();
    }
}
