package pe.gob.sunass.vma.security;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationManager implements AuthenticationManager{

	@Autowired
    private UserAuthenticationProvider provider;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		 Authentication result = provider.authenticate(authentication);
	     if (Objects.nonNull(result)) {
	    	 return result;
	     }
	     throw new ProviderNotFoundException("Authentication failed!");
	}
}
