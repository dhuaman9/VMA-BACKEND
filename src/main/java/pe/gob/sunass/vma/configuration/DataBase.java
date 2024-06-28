package pe.gob.sunass.vma.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef= "myAppEntityManagerFactory", 
		transactionManagerRef = "myAppTransactionManager",
		basePackages = {"pe.gob.sunass.vma.repository"})
public class DataBase {

	@Autowired
    private Environment environment;
	
    public static int responseCode = 0;
	
    
    @Bean(name="myAppCredential")
    @Profile("!local")
	public AppCredential appCredential() throws IOException {
		
    	String dataSourceRemoteSecretOps = environment.getProperty("myapp.datasource.remote-secret-ops");
    	String dataSourceToken           = environment.getProperty("myapp.datasource.token");
    	
		String urlSecretOps  = dataSourceRemoteSecretOps.concat("/all"); 
		String uuid          = dataSourceToken;
		
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
			    		case "PSQL_CONNSTRING": myAppCredential.setDbJdbcUrl(mySecretValue);
			    								break;
			    		case "PSQL_USERNAME": myAppCredential.setDbUsername(mySecretValue);
												break;
			    		case "PSQL_PASSWORD": myAppCredential.setDbPassword(mySecretValue);
												break;
			    		case "JWT_SECRETKEY": myAppCredential.setJwtSecretKey(mySecretValue);
												break;
			    	}
			    	
			    }
			    
			    myAppCredential.setDbDriverClassName(environment.getProperty("myapp.datasource.driver-class-name"));
			    
			    myAppCredential.setJpaDataBasePlatform(environment.getProperty("myapp.jpa.database-platform"));
			    myAppCredential.setJpaShowSql(environment.getProperty("myapp.jpa.show-sql"));
			}
		}
		
		return myAppCredential;
	}
    
    @Bean(name="myAppCredential")
    @Profile("local")
	public AppCredential appCredentialLocal() throws IOException {
    	
		AppCredential myAppCredential = new AppCredential();
		
		myAppCredential.setDbJdbcUrl(environment.getProperty("myapp.datasource.url"));
		myAppCredential.setDbUsername(environment.getProperty("myapp.datasource.username"));
		myAppCredential.setDbPassword(environment.getProperty("myapp.datasource.password"));
		myAppCredential.setDbDriverClassName(environment.getProperty("myapp.datasource.driver-class-name"));
	    
	    myAppCredential.setJpaDataBasePlatform(environment.getProperty("myapp.jpa.database-platform"));
	    myAppCredential.setJpaShowSql(environment.getProperty("myapp.jpa.show-sql"));
	    
	    myAppCredential.setJwtSecretKey(environment.getProperty("security.jwt.secret-key"));
		
		return myAppCredential;
	}
    
	@Bean(name="myAppDataSource")
	@ConfigurationProperties(prefix="myapp.datasource")
	public DataSource dataSource(@Qualifier("myAppCredential") AppCredential appCredential) throws IOException {
		
		return DataSourceBuilder.create().url(appCredential.getDbJdbcUrl())
				  .username(appCredential.getDbUsername())
				  .password(appCredential.getDbPassword())
				  .driverClassName(appCredential.getDbDriverClassName()).build();
	}
	
	
	@Bean(name="myAppEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(@Qualifier("myAppDataSource") DataSource dataSource, @Qualifier("myAppCredential") AppCredential appCredential){
		
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(dataSource);
		factoryBean.setPackagesToScan("pe.gob.sunass.vma.model");
		
		HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
		factoryBean.setJpaVendorAdapter(vendor);
		
		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.dialect", appCredential.getJpaDataBasePlatform());
		properties.put("hibernate.show_sql", appCredential.getJpaShowSql());
		factoryBean.setJpaPropertyMap(properties);
		
		return factoryBean;
	}
	
	@Bean(name="myAppTransactionManager")
	public PlatformTransactionManager platformTransactionManager(@Qualifier("myAppEntityManagerFactory") EntityManagerFactory entityManagerFactory){
		return new JpaTransactionManager(entityManagerFactory);
	}
	
	public static class AppCredential {
		
		private String dbJdbcUrl;
		private String dbUsername;
		private String dbPassword;
		private String dbDriverClassName;
		
		private String jpaShowSql;
		private String jpaDataBasePlatform;
		
		private String jwtSecretKey;
		
		public String getDbJdbcUrl() {
			return dbJdbcUrl;
		}
		public void setDbJdbcUrl(String dbJdbcUrl) {
			this.dbJdbcUrl = dbJdbcUrl;
		}
		public String getDbUsername() {
			return dbUsername;
		}
		public void setDbUsername(String dbUsername) {
			this.dbUsername = dbUsername;
		}
		public String getDbPassword() {
			return dbPassword;
		}
		public void setDbPassword(String dbPassword) {
			this.dbPassword = dbPassword;
		}
		public String getDbDriverClassName() {
			return dbDriverClassName;
		}
		public void setDbDriverClassName(String dbDriverClassName) {
			this.dbDriverClassName = dbDriverClassName;
		}

		public String getJpaShowSql() {
			return jpaShowSql;
		}
		public void setJpaShowSql(String jpaShowSql) {
			this.jpaShowSql = jpaShowSql;
		}
		public String getJpaDataBasePlatform() {
			return jpaDataBasePlatform;
		}
		public void setJpaDataBasePlatform(String jpaDataBasePlatform) {
			this.jpaDataBasePlatform = jpaDataBasePlatform;
		}
		
		public String getJwtSecretKey() {
			return jwtSecretKey;
		}
		public void setJwtSecretKey(String jwtSecretKey) {
			this.jwtSecretKey = jwtSecretKey;
		}
	}
}
