package pe.gob.sunass.vma.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import pe.gob.sunass.vma.configuration.DataBase.AppCredential;
import pe.gob.sunass.vma.security.jwt.JWTBean;

@Component
@Configuration
public class SecurityBeansInjector {

	@Value("${cors.urls}")
	private String CORS_URL;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JWTBean jwtBean(@Qualifier("myAppCredential") AppCredential appCredential) {
		return new JWTBean(appCredential.getJwtSecretKey());
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		CorsConfiguration config = new CorsConfiguration();

		List<String> myAllowedOrigins = new ArrayList<>();

		String urls = CORS_URL;

		if (urls != null) {
			for (String url : urls.split(",")) {

				if (!url.isEmpty()) {
					myAllowedOrigins.add(url.trim());
				}
			}
		}

		if ((!myAllowedOrigins.isEmpty()) && myAllowedOrigins.size() > 0) {
			config.setAllowedOrigins(myAllowedOrigins);
		} else {

			config.setAllowedOriginPatterns(Collections.singletonList("*"));
			config.setAllowedOrigins(Collections.singletonList("*"));
		}

		/*
		 * config.setAllowedOriginPatterns(Collections.singletonList("*"));
		 * config.setAllowedOrigins(Collections.singletonList("*"));
		 */

		config.setAllowedMethods(Collections.singletonList("*"));
		config.setAllowedHeaders(Collections.singletonList("*"));

		source.registerCorsConfiguration("/**", config);

		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

}
