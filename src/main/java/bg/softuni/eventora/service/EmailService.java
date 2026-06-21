package bg.softuni.eventora.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(String to, String subject, String text) {
        if (fromEmail == null || fromEmail.isBlank() || fromEmail.equals("YOUR_EMAIL@gmail.com")
                || mailPassword == null || mailPassword.isBlank() || mailPassword.equals("YOUR_APP_PASSWORD")) {
            throw new IllegalStateException("Имейлът не е настроен. В application.properties сложи реален spring.mail.username и Gmail App Password.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
