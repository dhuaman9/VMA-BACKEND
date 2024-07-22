package pe.gob.sunass.vma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.sunass.vma.service.RegistroVMAService;
import pe.gob.sunass.vma.util.ResponseEntity;

import java.util.Collection;

@RestController
@RequestMapping("/anexos")
public class AnexoController {

    @Autowired
    private RegistroVMAService registroVMAService;

    @GetMapping("/registros-vma")
    public ResponseEntity<?> getAnexos(@RequestParam(name = "anio") String anio) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            authorities.forEach(authority -> System.out.println("Role: " + authority.getAuthority()));
        }

        return ResponseEntity.ok(registroVMAService.listaDeAnexosRegistrosVmaDTO(anio));
    }
}
