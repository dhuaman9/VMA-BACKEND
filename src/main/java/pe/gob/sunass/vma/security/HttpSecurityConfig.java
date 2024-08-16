package pe.gob.sunass.vma.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import pe.gob.sunass.vma.security.jwt.JWTAuthenticationEntryPoint;
import pe.gob.sunass.vma.security.jwt.JWTAuthenticationFilter;

@Component
@EnableWebSecurity
@EnableMethodSecurity
public class HttpSecurityConfig {
	
	
	@Autowired
    private JWTAuthenticationFilter authenticationFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    	http.csrf().disable()
				.httpBasic().disable()
				.cors()
				.and()
				.authorizeRequests(configurer -> configurer
						.antMatchers("/error").permitAll()
						.antMatchers("http://apps.sunass.gob.pe/").permitAll()
						.antMatchers("/", "/home/index", "/swagger/**", "/swagger-ui/**", "/swagger-resources/**", "/swagger.json/**").permitAll()
						.antMatchers(HttpMethod.POST, "/auth/**").permitAll()
						.antMatchers("/auth/admin-access").hasAuthority("Administrador OTI")
						.antMatchers("/anexos/**").hasAnyAuthority("ADMINISTRADOR DAP", "CONSULTOR", "REGISTRADOR")
						.anyRequest().authenticated()
						)
				.exceptionHandling()
				.authenticationEntryPoint(
						(request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
								"Unauthorized"))
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.headers().frameOptions().sameOrigin();
		http.addFilterBefore(authenticationFilter, UserAuthenticationFilter.class);
		http.exceptionHandling().authenticationEntryPoint(new JWTAuthenticationEntryPoint());
		
		return http.build();
    }
}
