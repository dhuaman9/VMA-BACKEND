package pe.gob.sunass.vma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.gob.sunass.vma.exception.ResourceNotFoundException;
import pe.gob.sunass.vma.model.TokenPassword;
import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.repository.TokenPasswordRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenPasswordService {
    private final TokenPasswordRepository tokenPasswordRepository;

    @Value("${myapp.dias-expiracion}")
    private int dias_expiracion;

    @Autowired
    public TokenPasswordService(TokenPasswordRepository tokenPasswordRepository) {
        this.tokenPasswordRepository = tokenPasswordRepository;
    }

    public String crearToken(Usuario usuario) {
        TokenPassword tokenPassword = new TokenPassword();
        tokenPassword.setToken(generarToken());
        tokenPassword.setFechaExpiracion(LocalDateTime.now().plusDays(dias_expiracion)); //para dev o prod
        //tokenPassword.setFechaExpiracion(LocalDateTime.now().plusMinutes(dias_expiracion));//para probar en  local
        tokenPassword.setUsuario(usuario);
        tokenPassword.setCompletado(false);
        tokenPasswordRepository.save(tokenPassword);
        return tokenPassword.getToken();
    }

    public String actualizarTiempoToken(Integer userId) {
        TokenPassword token = tokenPasswordRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Token no encontrado")); //interfaz funcional que usa expresion lambda 
        
        token.setCompletado(false);
        token.setFechaExpiracion(LocalDateTime.now().plusDays(dias_expiracion));
        //token.setFechaExpiracion(LocalDateTime.now().plusMinutes(dias_expiracion)); //en local
        return tokenPasswordRepository.save(token).getToken();
    }

    public TokenPassword findTokenByToken(String token) {
        return tokenPasswordRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token no encontrado"));
    }

    public TokenPassword findTokenByUserId(Integer userId) {
        return tokenPasswordRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Token no encontrado"));
    }

    public void save(TokenPassword tokenPassword) {
        tokenPasswordRepository.save(tokenPassword);
    }

    private String generarToken() {
        return UUID.randomUUID().toString();
    }
}
