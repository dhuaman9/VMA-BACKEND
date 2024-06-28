package pe.gob.sunass.vma.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class UserAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	public static final String FORMDOMAINKEY = "hdnDomain";
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
	public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
	public UserAuthenticationFilter() {
		this.setUsernameParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
		this.setPasswordParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
	}
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) 
        throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        UserAuthenticationToken authRequest = getAuthRequest(request);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private UserAuthenticationToken getAuthRequest(HttpServletRequest request) {
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        String domain = obtainDomain(request);

        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (domain == null) {
            domain = "";
        }

        String usernameDomain = String.format("%s%s", username.trim(), domain);
        return new UserAuthenticationToken(usernameDomain, password, domain);        
    }

    private String obtainDomain(HttpServletRequest request) {
        return request.getParameter(FORMDOMAINKEY);
    }
}
