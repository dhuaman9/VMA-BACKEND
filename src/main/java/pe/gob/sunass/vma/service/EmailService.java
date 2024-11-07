package pe.gob.sunass.vma.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import pe.gob.sunass.vma.configuration.ConfigProperties;
import pe.gob.sunass.vma.dto.UsuarioDTO;
import pe.gob.sunass.vma.model.Usuario;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

@Service
public class EmailService {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(EmailService.class);
	
    @Value("${myapp.app-front-url}")
    private String appUrl;
    
	private final JavaMailSender mailSender;   // en local
    
    @Autowired
	ConfigProperties propiedadesMail;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    
    // metodo para probar en local
    public void sendEmail(UsuarioDTO usuario, String token) throws MessagingException, IOException {
        
        InputStream inputStream = null;
     // Construir la URL completa con el contexto y el token
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //String htmlContent = new String(Files.readAllBytes(Paths.get("src/main/resources/email/mail-template.html")));
        
        inputStream = getClass().getClassLoader().getResourceAsStream("email/mail-template.html");
            if (inputStream == null) {
                throw new IOException("No se pudo encontrar la plantilla HTML en el classpath: email/mail-template.html");
            }
        String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        String changePasswordLink = String.format("%s/recuperar-password/%s", appUrl, token);

     // Construir la URL completa con el contexto y el token
       
        htmlContent = htmlContent.replace("[[${fullname}]]", String.format("%s %s", usuario.getNombres(), usuario.getApellidos()))
                .replace("[[${password}]]", usuario.getPassword())
                .replace("[[${username}]]", usuario.getUserName())
                .replace("[[${changePasswordLink}]]", changePasswordLink);
        
        helper.setTo(usuario.getCorreo());
        helper.setSubject("Credenciales VMA");
        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }

    public void enviarMailActualizarToken(Usuario usuario, String token) throws MessagingException, IOException {
        InputStream inputStream = null;

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        inputStream = getClass().getClassLoader().getResourceAsStream("email/mail-template-actualizar-token.html");
        if (inputStream == null) {
            throw new IOException("No se pudo encontrar la plantilla HTML en el classpath: email/mail-template-actualizar-token.html");
        }

        String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        String changePasswordLink = String.format("%s/recuperar-password/%s", appUrl, token);

        // Construir la URL completa con el contexto y el token
        htmlContent = htmlContent.replace("[[${fullname}]]", String.format("%s %s", usuario.getNombres(), usuario.getApellidos()))
                .replace("[[${changePasswordLink}]]", changePasswordLink);

        helper.setTo(usuario.getCorreo());
        helper.setSubject("Cambiar contraseña VMA");
        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }
    
    
    //metodo funcional en dev y producccion
    /* public void sendEmail(UsuarioDTO usuario, String token) throws MessagingException, IOException {
    	
    	InputStream inputStream = null;
    //	String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();  //obtiene la url base  del backend
    	
        try {
            String servidorSmtp = propiedadesMail.getSmtphost();
            String emisionCorreo = propiedadesMail.getRemitente();

            // Cargar y personalizar la plantilla HTML
           // String htmlContent = new String(Files.readAllBytes(Paths.get("src/main/resources/email/mail-template.html")));
            
         // Cargar y personalizar la plantilla HTML desde el classpath
            inputStream = getClass().getClassLoader().getResourceAsStream("email/mail-template.html");
            if (inputStream == null) {
                throw new IOException("No se pudo encontrar la plantilla HTML en el classpath: email/mail-template.html");
            }
            
            String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String changePasswordLink = String.format("%s/restablecer-password/%s", appUrl, token);
            
            htmlContent = htmlContent.replace("[[${fullname}]]", String.format("%s %s", usuario.getNombres(), usuario.getApellidos()))
                    .replace("[[${password}]]", usuario.getPassword())
                    .replace("[[${username}]]", usuario.getUserName())
                    .replace("[[${changePasswordLink}]]", changePasswordLink);

            // Configuración de propiedades para SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", servidorSmtp);
            props.put("mail.smtp.connectiontimeout", "50000"); //tiempo máximo, en milisegundos, que el cliente de correo  debe esperar para tener  una conexión con el servidor SMTP.
            props.put("mail.smtp.timeout", "50000");  // el tiempo máximo en milisegundos que JavaMail esperará por una respuesta del servidor SMTP 

            // Crear la sesión SMTP
            Session session = Session.getInstance(props, null);

            // Construir el mensaje de correo en formato HTML
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(emisionCorreo));
            msg.setSubject("Credenciales de acceso al sistema VMA", "UTF-8");
            
            // Establecer el contenido HTML
            msg.setContent(htmlContent, "text/html; charset=UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(usuario.getCorreo(), false));

            // Enviar el mensaje
            Transport.send(msg);

        } catch (SendFailedException se) {
            logger.error("Error al enviar correo: fallo por relay: " + se.getMessage());
        } catch (Exception e) {
            logger.error("Error al enviar correo: " + e.getMessage());
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("Error al cerrar el InputStream: " + e.getMessage());
                }
            }
        }
    }*/
    
    
    
}
