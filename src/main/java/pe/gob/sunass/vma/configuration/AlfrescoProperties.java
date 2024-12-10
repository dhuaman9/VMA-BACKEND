package pe.gob.sunass.vma.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@PropertySource("classpath:alfresco.properties")
public class AlfrescoProperties {

	@Value("${alfresco.url}")
    private String url;
    
    @Value("${alfresco.user}")
    private String user;
    
    @Value("${alfresco.password}")
    private String password;
    
    @Value("${alfresco.carpeta}")
    private String carpeta;
    
    @Value("${alfresco.SpaceStore}")
    private String spaceStore;

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

	public String getCarpeta() {
		return carpeta;
	}

	public void setCarpeta(String carpeta) {
		this.carpeta = carpeta;
	}

	public String getSpaceStore() {
		return spaceStore;
	}

	public void setSpaceStore(String spaceStore) {
		this.spaceStore = spaceStore;
	}
	
	
}
