package pe.gob.sunass.vma.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pe.gob.sunass.vma.configuration.DataBase.AppCredential;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Configuration
public class AlfrescoProperties {

	@Autowired
    private Environment environment;
	
    public static int responseCode = 0;
	
    
    @Bean(name="myAppCredentialAlfresco")
    @Profile("!local")
	public AppCredential appCredential() throws IOException {
		
    	String alfrescoRemoteSecretOps = environment.getProperty("myapp.alfresco.remote-secret-ops");
    	String alfrescoToken           = environment.getProperty("myapp.alfresco.token");
    	
		String urlSecretOps  = alfrescoRemoteSecretOps.concat("/all"); 
		String uuid          = alfrescoToken;
		
		String jsonConnStr = null;
		
		AppCredential myAppCredential = new AppCredential();
		
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		
		MediaType mediaType = MediaType.parse("application/json");
		
		RequestBody body = RequestBody.create(mediaType, "{\r\n  \"uuid\":\""+uuid+"\"}");
		
		Request request = new Request.Builder()
				  .url(urlSecretOps)
				  .method("POST", body)
				  .addHeader("Content-Type", "application/json")
				  .build();
		
		Response response = client.newCall(request).execute();

		if ((responseCode = response.code()) == 200) {
		
			jsonConnStr = response.body().string();

			JSONArray jsonArray = new JSONArray(jsonConnStr);
			
			if (jsonArray != null) {   
			      
			    for (int i=0;i<jsonArray.length();i++){
			    	
			    	JSONObject jsonObject = jsonArray.getJSONObject(i);
			    	
			    	String mySecretName  = jsonObject.get("secretName").toString();
			    	String mySecretValue = jsonObject.get("secretValue").toString();
			    
			    	switch(mySecretName) {
			    		case "ALFRESCO_HOST": myAppCredential.setAlfrescoHost(mySecretValue);
			    								break;
			    		case "ALFRESCO_USER": myAppCredential.setAlfrescoUsername(mySecretValue);
												break;
			    		case "ALFRESCO_PASSWORD": myAppCredential.setAlfrescoPassword(mySecretValue);
												break;
			    		case "ALFRESCO_SPACESTORE": myAppCredential.setAlfrescoSpaceStore(mySecretValue);
												break;
			    	}
			    	
			    }
			}
		}
		
		return myAppCredential;
	}
    @Bean(name="myAppCredentialAlfresco")
    @Profile("local")
	public AppCredential appCredentialLocal() throws IOException {
    	
		AppCredential myAppCredential = new AppCredential();
		
		myAppCredential.setAlfrescoHost(environment.getProperty("myapp.alfresco.url"));
		myAppCredential.setAlfrescoUsername(environment.getProperty("myapp.alfresco.user"));
		myAppCredential.setAlfrescoPassword(environment.getProperty("myapp.alfresco.password"));
		myAppCredential.setAlfrescoSpaceStore(environment.getProperty("myapp.alfresco.spaceStore"));
		////myAppCredential.setAlfrescoCarpeta(environment.getProperty("myapp.alfresco.carpeta"));
	  
		
		return myAppCredential;
	}
    

	public static class AppCredential {
		
		private String alfrescoHost;
		private String alfrescoUsername;
		private String alfrescoPassword;
		private String alfrescoSpaceStore;
		
		public String getAlfrescoHost() {
			return alfrescoHost;
		}
		public void setAlfrescoHost(String alfrescoHost) {
			this.alfrescoHost = alfrescoHost;
		}
		public String getAlfrescoUsername() {
			return alfrescoUsername;
		}
		public void setAlfrescoUsername(String alfrescoUsername) {
			this.alfrescoUsername = alfrescoUsername;
		}
		public String getAlfrescoPassword() {
			return alfrescoPassword;
		}
		public void setAlfrescoPassword(String alfrescoPassword) {
			this.alfrescoPassword = alfrescoPassword;
		}
		public String getAlfrescoSpaceStore() {
			return alfrescoSpaceStore;
		}
		public void setAlfrescoSpaceStore(String alfrescoSpaceStore) {
			this.alfrescoSpaceStore = alfrescoSpaceStore;
		}
		
	}
}
