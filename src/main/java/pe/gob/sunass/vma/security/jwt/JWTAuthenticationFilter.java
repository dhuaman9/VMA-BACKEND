package pe.gob.sunass.vma.security.jwt;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import pe.gob.sunass.vma.exception.BadRequestException;
import pe.gob.sunass.vma.exception.UnAuthorizedException;
import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.repository.UsuarioRepository;
import pe.gob.sunass.vma.util.DefaultMessage;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
    private JWTProvider _jwtProvider;

    @Autowired
    private UsuarioRepository _usuarioRepository;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String jwt = "";
		
		try {
			
			//1. Obtener el header que contiene el jwt
	        String authHeader = request.getHeader("Authorization"); // Bearer jwt
	
	        if(authHeader == null || !authHeader.startsWith("Bearer ")){
	            filterChain.doFilter(request, response);
	            return;
	        }
	
	        //2. Obtener jwt desde header
	        jwt = authHeader.split(" ")[1];
	        
	        //3. Obtener subject/username desde el jwt
	        String username = _jwtProvider.extractUsername(jwt);
	        
	        //4. Setear un objeto Authentication dentro del SecurityContext
	        Usuario user = _usuarioRepository.findByUserName(username).get();
	        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
	                username, null, user.getAuthorities()
	        );
	        SecurityContextHolder.getContext().setAuthentication(authToken);
        
		}
		catch(Exception ex) {
    		if (ex instanceof ExpiredJwtException) {
    			
    			boolean enabledToRefresh = false;
    			
    			try {
					if(_jwtProvider.isEnabledToRefresh(jwt)) {
						enabledToRefresh = true;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    			if(enabledToRefresh)
    			{
    				response.setStatus(HttpStatus.UNAUTHORIZED.value());
    			}
    			else {
    				throw new UnAuthorizedException(DefaultMessage.ServerStatus.S401);
    			}
    			
			} else {
				throw new BadRequestException(DefaultMessage.ServerStatus.S400);
			}
			
    	}
		
		//5. Ejecutar el restro de filtros
        filterChain.doFilter(request, response);
	}
	
	public String getToken(HttpServletRequest request) {
		
        String authHeader = request.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")){
        	return authHeader.split(" ")[1];
        }
        return null;
	}
}
