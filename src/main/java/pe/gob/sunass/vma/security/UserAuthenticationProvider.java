package pe.gob.sunass.vma.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.service.UsuarioService;

@Component
public class UserAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private UsuarioService _usuarioService;

	private String userNotFoundEncodedPassword;

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");
			throw new BadCredentialsException(
					messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}

		String presentedPassword = authentication.getCredentials().toString();

		if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
			logger.debug("Authentication failed: password does not match stored value");
			throw new BadCredentialsException(
					messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}
	}

	@Override
	protected void doAfterPropertiesSet() throws Exception {
		Assert.notNull(this._usuarioService, "A UserDetailsService must be set");
		this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {

		UserAuthenticationToken authentication2 = (UserAuthenticationToken) authentication;
		String userName = (String) authentication2.getPrincipal();
		final String password = (String) authentication2.getCredentials();
		final String domain = authentication2.getDomain();

		Usuario userRs = null;
		List<GrantedAuthority> authorities = new ArrayList<>();

		try {

			Optional<Usuario> optUsuario = _usuarioService.findByUserNameLDAP(username, password,
					passwordEncoder.encode(password));

			if (optUsuario.isPresent()) {

				userRs = optUsuario.get();

				if (!userRs.isEnabled()) {
					throw new DisabledException("El usuario está deshabilitado");
				}

				if (!passwordEncoder.matches(password, userRs.getPassword())) {
					throw new BadCredentialsException("Usuario y/o contraseña incorrecto");
				} else {
					authorities.add(new SimpleGrantedAuthority(userRs.getRole().getAuth()));
				}
			} else {
				throw new BadCredentialsException("Usuario y/o contraseña incorrecto");
			}

		} catch (UsernameNotFoundException ex) {
			if (password != null) {
				String presentedPassword = authentication.getCredentials().toString();
				passwordEncoder.matches(presentedPassword, userNotFoundEncodedPassword);
			}
			throw ex;
		} catch (Exception ex) {
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
		}

		return new User(userRs.getUserName(), userRs.getPassword(), authorities);

		// return _usuarioService.findByUserNameLDAP(username, password,
		// passwordEncoder.encode(password)).orElseThrow(() -> new
		// RuntimeException("User not found"));
	}

}
