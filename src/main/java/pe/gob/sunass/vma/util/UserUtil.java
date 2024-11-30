package pe.gob.sunass.vma.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.repository.UsuarioRepository;

import java.util.Optional;

@Component
public class UserUtil {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        Optional<Usuario> user = usuarioRepository.findByUserName(username);
        return user.get().getId();//Accedemos al optional directo sin validar si es null porque siempre habrá un usuario logeado
    }
    
    //para  obtener el  id de la empresa del usuario en sesion.
    public Integer getCurrentUserIdEmpresa() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        Optional<Usuario> user = usuarioRepository.findByUserName(username);
        return user.get().getEmpresa().getIdEmpresa();
    }
    
    
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        Optional<Usuario> user = usuarioRepository.findByUserName(username);
        return user.get().getUserName();
    }
    
    
}