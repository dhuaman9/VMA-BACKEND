package pe.gob.sunass.vma.configuration;


import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.servlet.MultipartConfigElement;

@Configuration
public class MultipartConfig {

//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        // Tamaño máximo permitido para cada archivo individual (10MB en este ejemplo)
//        factory.setMaxFileSize("10MB");
//        // Tamaño máximo total permitido para la solicitud (10MB en este ejemplo)
//        factory.setMaxRequestSize("10MB");
//        return factory.createMultipartConfig();
//    }
}