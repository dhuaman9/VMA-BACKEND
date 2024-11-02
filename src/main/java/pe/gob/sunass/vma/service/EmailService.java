package pe.gob.sunass.vma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pe.gob.sunass.vma.dto.UsuarioDTO;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${myapp.front-url}")
    private String appUrl;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(UsuarioDTO usuario, String token) throws MessagingException, IOException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        String htmlContent = new String(Files.readAllBytes(Paths.get("src/main/resources/email/mail-template.html")));

        htmlContent = htmlContent.replace("[[${fullname}]]", String.format("%s %s", usuario.getNombres(), usuario.getApellidos()))
                .replace("[[${password}]]", usuario.getPassword())
                .replace("[[${username}]]", usuario.getUserName())
                .replace("[[${changePasswordLink}]]", String.format("%s/recuperar-password/%s",appUrl, token));

        helper.setTo(usuario.getCorreo());
        helper.setSubject("Credenciales VMA");
        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }
}
