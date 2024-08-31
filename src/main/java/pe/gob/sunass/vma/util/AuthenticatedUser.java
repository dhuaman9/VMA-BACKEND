package pe.gob.sunass.vma.util;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import pe.gob.sunass.vma.model.Usuario;


public class AuthenticatedUser {

	
//	public static Integer getAuthenticatedUserId() {
//	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//	    if (authentication != null && authentication.isAuthenticated()) {
//	        Object principal = authentication.getPrincipal();
//	        if (principal instanceof Usuario) {
//	            return ((Usuario) principal).getId();
//	            
//	        }
//	    }
//	    return null;  // se  puedes manejar de alguna otra forma cuando no hay usuario
//	}
	
}
