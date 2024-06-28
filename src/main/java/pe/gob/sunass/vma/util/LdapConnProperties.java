package pe.gob.sunass.vma.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:ldap.properties")
public class LdapConnProperties {
	
	
	@Value("${ldap.prefix}")
	private String prefix;
	@Value("${ldap.timeout}")
	private String timeout;
	@Value("${ldap.url}")
	private String url;
	@Value("${ldap.user}")
	private String user;
	@Value("${ldap.password}")
	private String password;
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getTimeout() {
		return timeout;
	}
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigLdapInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
	
	

}
