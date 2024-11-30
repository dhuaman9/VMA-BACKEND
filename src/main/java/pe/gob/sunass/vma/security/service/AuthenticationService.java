package pe.gob.sunass.vma.security.service;

import java.text.ParseException;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import pe.gob.sunass.vma.exception.PasswordNoCambiadoException;
import pe.gob.sunass.vma.model.TokenPassword;
import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.repository.UsuarioRepository;
import pe.gob.sunass.vma.security.UserAuthenticationManager;
import pe.gob.sunass.vma.security.UserAuthenticationToken;
import pe.gob.sunass.vma.security.dto.AuthenticationRequestDTO;
import pe.gob.sunass.vma.security.dto.AuthenticationResponseDTO;
import pe.gob.sunass.vma.security.dto.JwtTokenDTO;
import pe.gob.sunass.vma.security.jwt.JWTProvider;
import pe.gob.sunass.vma.service.TokenPasswordService;

@Service
public class AuthenticationService {

	@Autowired
    private UserAuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JWTProvider _jwtProvider;

	@Autowired
    private TokenPasswordService tokenPasswordService;

    public AuthenticationResponseDTO login(AuthenticationRequestDTO authRequest) {
    	
    	UserAuthenticationToken authInputToken = new UserAuthenticationToken((Object) authRequest.getUsername(), (Object) authRequest.getPassword());

    	Authentication authentication = authenticationManager.authenticate(authInputToken);
    	
    	SecurityContextHolder.getContext().setAuthentication(authentication);
    	
    	String userName = (String) authInputToken.getPrincipal();
    	
        Usuario user = this.findByUsername(userName).get();

		if(Boolean.FALSE.equals(user.getPasswordCambiado())) {
			TokenPassword token = tokenPasswordService.findTokenByUserId(user.getId());
			throw new PasswordNoCambiadoException("El usuario no ha cambiado su contraseña", token.getToken());
		}

        String jwt = _jwtProvider.generateToken(user);

        return new AuthenticationResponseDTO(jwt);
    }
    
    public Optional<Usuario> findByUsername(String username) {
    	
    	return usuarioRepository.findByUserName(username);
    }
    
    
    public String getUsername() {
	   // Solo funciona cuando hay sesion activa
       SecurityContext context = SecurityContextHolder.getContext();
	   
       Authentication authentication = context.getAuthentication();
	   
       if (authentication == null)
	     return null;
	   
       Object principal = authentication.getPrincipal();
	   if (principal instanceof UserDetails) {
	     return ((UserDetails) principal).getUsername();
	   } else {
	     return principal.toString();
	   }
    }
    
    public String getUsernameFromExpired(JwtTokenDTO jwtTokenDTO) throws ParseException {
 	  
    	return _jwtProvider.extractUsernameFromExpired(jwtTokenDTO.getJwt());
     }
    
    public AuthenticationResponseDTO refreshToken(JwtTokenDTO jwtTokenDTO) throws ParseException {
    	
    	String userName = this.getUsernameFromExpired(jwtTokenDTO);
    	
    	Usuario user = this.findByUsername(userName).get();
    	
    	String jwt = null;
    	
    	boolean enabledToRefresh = false;
		
		try {
			if(_jwtProvider.isEnabledToRefresh(jwtTokenDTO.getJwt())) {
				enabledToRefresh = true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(enabledToRefresh)
		{
			jwt = _jwtProvider.refreshToken(jwtTokenDTO, user);
		}
    	
    	return new AuthenticationResponseDTO(jwt);
    }
	
    
    public String getToken(HttpServletRequest request) {
		
        String authHeader = request.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")){
        	return authHeader.split(" ")[1];
        }
        return null;
	}
}
