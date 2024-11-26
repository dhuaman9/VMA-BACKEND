package pe.gob.sunass.vma;


import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
@PropertySource("classpath:alfresco.properties")
@EnableScheduling
public class VmaApplication extends SpringBootServletInitializer{

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(VmaApplication.class);
    }
	
	public static void main(String[] args) {
		SpringApplication.run(VmaApplication.class, args);
	}
	

	
	@Profile({"dev","local"})
	@Bean
	public CommandLineRunner createPasswordsCommand(){
		
		String originalInput = "D3v3l0p3r 3n SuN4SS V4M0567 123456789 ABCabc";
		Base64 base64 = new Base64();
		String encodedString = new String(base64.encode(originalInput.getBytes()));
		
		return args -> {
			System.out.println(encodedString);
			System.out.println(passwordEncoder.encode("clave123"));
			System.out.println(passwordEncoder.encode("clave456"));
			System.out.println(passwordEncoder.encode("clave789"));
			System.out.println(passwordEncoder.encode("clave123"));

			System.out.println(passwordEncoder.matches("clave123","$2a$10$v69WhacenRdHFFnK4u4LR.SpGV4Hrvlcg4S4ED5wkDHbQhg7KHH9S"));
		};
	}

}
