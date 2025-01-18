package pe.gob.sunass.vma.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import pe.gob.sunass.vma.configuration.ConfigProperties;
import pe.gob.sunass.vma.dto.UsuarioDTO;
import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Service
public class EmailService {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(EmailService.class);
	
//    @Value("${myapp.app-front-url}")
//    private String appUrl;
    
//	private final JavaMailSender mailSender;   // probar en local
    
    @Autowired
	ConfigProperties propiedadesMail;

//    @Autowired
//    public EmailService(JavaMailSender mailSender) {  //probar  en local
//        this.mailSender = mailSender;
//    }

    
    //metodo funcional en dev , qa  o producccion
    public void sendEmail(RegistroVMA registrovma) throws MessagingException, IOException {
   	
     logger.info("entrando al metodo  sendEmail");
   	 InputStream inputStream = null;
  
       try {
           String servidorSmtp = propiedadesMail.getSmtphost();
           String emisionCorreo = propiedadesMail.getRemitente();
           String correoCc = propiedadesMail.getConCopia(); // Leer el correo CC del properties
           
           logger.info("correo del Remitente: " +propiedadesMail.getRemitente());
           //logger.info("password del Remitente: " +propiedadesMail.getPassword());
           logger.info("smtphost: "+ propiedadesMail.getSmtphost());
           logger.info("correo con copia a: "+ propiedadesMail.getConCopia());
         
           
        // Cargar y personalizar la plantilla HTML desde el classpath
           inputStream = getClass().getClassLoader().getResourceAsStream("email/mail-template.html");
           if (inputStream == null) {
               throw new IOException("No se pudo encontrar la plantilla HTML en el classpath: email/mail-template.html");
           }
           
           String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
           
           SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
           
           htmlContent = htmlContent.replace("[[${fullname}]]", registrovma.getNombreCompleto())
                   .replace("[[${eps}]]", registrovma.getEmpresa().getNombre())
                   .replace("[[${fecha_hora}]]", 
                	        registrovma.getUpdatedAt() != null 
                	            ? formatter.format(registrovma.getUpdatedAt()) 
                	            : formatter.format(registrovma.getCreatedAt()));

           // Configuración de propiedades para SMTP
           Properties props = new Properties();
           props.put("mail.smtp.host", servidorSmtp);
           props.put("mail.smtp.connectiontimeout", "40000"); //tiempo máximo, en milisegundos, que el cliente de correo  debe esperar para tener  una conexión con el servidor SMTP.
           props.put("mail.smtp.timeout", "40000");  // el tiempo máximo en milisegundos que JavaMail esperará por una respuesta del servidor SMTP 

           // Crear la sesión SMTP
           Session session = Session.getInstance(props, null);

           // Construir el mensaje de correo en formato HTML
           MimeMessage msg = new MimeMessage(session);
           msg.setFrom(new InternetAddress(emisionCorreo));
           msg.setSubject("Confirmación de Registro Completo del Informe Anual de VMA", "UTF-8");
           
           // Establecer el contenido HTML
           msg.setContent(htmlContent, "text/html; charset=UTF-8");
           msg.setSentDate(new Date());

           // Configurar los destinatarios principales
           msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(registrovma.getEmail(), false));

           // Configurar los destinatarios en copia (CC)
           if (correoCc != null && !correoCc.isEmpty()) {
               msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(correoCc, false));
           }

           // Enviar el mensaje
           Transport.send(msg);
           logger.info("Correo enviado correctamente a " + registrovma.getEmail() + " con copia a " + correoCc);

       } catch (SendFailedException se) {
           logger.error("Error al enviar correo: fallo por relay: " + se.getMessage());
       } catch (MessagingException ex) {
		    logger.error("Error relacionado con el servidor de correo: {}", ex.getMessage());
		    logger.info("validar los valores del correo.properties...");
		    throw new MessagingException("Se ha registrado correctamente, pero no se pudo enviar el  correo electrónico de confirmacion. Por favor, "
		    		+ "contacte al administrador o a soporte técnico.");
		} catch (Exception e) {
           logger.error("Error al enviar correo: " + e.getMessage());
           logger.info("validar los valores del correo.properties...");
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
   }
    
 
}
