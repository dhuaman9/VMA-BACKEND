package pe.gob.sunass.vma.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:correo.properties")
public class ConfigProperties {
	
	@Value("${mail.remitente}")
	private String remitente;
	
	@Value("${mail.password}")
	private String password;
	
	@Value("${mail.smtphost}")
	private String smtphost;
	
	@Value("${mail.autor}")
	private String autor;
	
	
	public String getRemitente() {
		return remitente;
	}
	public void setRemitente(String remitente) {
		this.remitente = remitente;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSmtphost() {
		return smtphost;
	}
	public void setSmtphost(String smtphost) {
		this.smtphost = smtphost;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	
	
	@Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
