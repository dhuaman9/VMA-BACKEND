package pe.gob.sunass.vma.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class UserAuthenticationToken extends UsernamePasswordAuthenticationToken{

	private static final long serialVersionUID = 1L;
	private String domain;
	public UserAuthenticationToken(Object principal, Object credentials) {
		super(principal, credentials);
	}
	public UserAuthenticationToken(Object principal, Object credentials, String domain) {
		super(principal, credentials);
		this.domain = domain;
		super.setAuthenticated(false);
	}

	public UserAuthenticationToken(Object principal, Object credentials, String domain,
			Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
		this.domain = domain;
		super.setAuthenticated(true); // must use super, as we override
	}

	public String getDomain() {
		return this.domain;
	}
}
