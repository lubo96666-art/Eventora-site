package bg.softuni.eventora.service;

import bg.softuni.eventora.dto.*;
import bg.softuni.eventora.model.*;
import bg.softuni.eventora.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@org.springframework.transaction.annotation.Transactional
public class AuthService {
    private final UserRepository users;
    private final VerificationTokenRepository verificationTokens;
    private final PasswordResetTokenRepository resetTokens;
    private final EmailService emailService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Value("${app.base-url}") private String baseUrl;

    public AuthService(UserRepository users, VerificationTokenRepository verificationTokens, PasswordResetTokenRepository resetTokens, EmailService emailService) {
        this.users = users; this.verificationTokens = verificationTokens; this.resetTokens = resetTokens; this.emailService = emailService;
    }

    public String register(RegisterDto dto) {
        if (users.findByEmail(dto.getEmail()).isPresent()) throw new IllegalArgumentException("Вече има потребител с този имейл.");
        if (users.findByUsername(dto.getUsername()).isPresent()) throw new IllegalArgumentException("Вече има потребител с това потребителско име.");

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(encoder.encode(dto.getPassword()));
        user.setEnabled(false);
        users.save(user);

        String code = generateCode();
        VerificationToken verificationCode = new VerificationToken();
        verificationCode.setCode(code);
        verificationCode.setToken(UUID.randomUUID().toString());
        verificationCode.setUser(user);
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        verificationTokens.save(verificationCode);

        emailService.send(
                user.getEmail(),
                "Код за потвърждение в Eventora",
                "Здравей, " + user.getUsername() + "!\n\n" +
                        "Твоят код за потвърждение е: " + code + "\n\n" +
                        "Въведи този код в сайта, за да активираш профила си.\n" +
                        "Кодът е валиден 15 минути."
        );
        return user.getEmail();
    }

    public User verifyCode(VerifyCodeDto dto) {
        VerificationToken verificationCode = verificationTokens
                .findFirstByCodeAndUser_Email(dto.getCode(), dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Невалиден код за потвърждение."));

        if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Кодът е изтекъл. Регистрирай се отново или поискай нов код.");
        }

        User user = verificationCode.getUser();
        user.setEnabled(true);
        users.save(user);
        verificationTokens.delete(verificationCode);
        return user;
    }

    public void resendVerificationCode(String email) {
        User user = users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Няма потребител с този имейл."));
        if (user.isEnabled()) throw new IllegalArgumentException("Профилът вече е активиран.");

        verificationTokens.findFirstByUser_Email(email).ifPresent(verificationTokens::delete);
        String code = generateCode();
        VerificationToken verificationCode = new VerificationToken();
        verificationCode.setCode(code);
        verificationCode.setToken(UUID.randomUUID().toString());
        verificationCode.setUser(user);
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        verificationTokens.save(verificationCode);

        emailService.send(
                user.getEmail(),
                "Нов код за потвърждение в Eventora",
                "Здравей!\n\nНовият ти код за потвърждение е: " + code + "\n\nКодът е валиден 15 минути."
        );
    }

    private String generateCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    public void login(LoginDto dto, HttpSession session) {
        String identifier = dto.getEmail().trim();
        User user = users.findByEmail(identifier)
                .or(() -> users.findByUsername(identifier))
                .orElseThrow(() -> new IllegalArgumentException("Грешен имейл/username или парола."));
        if (!user.isEnabled()) throw new IllegalArgumentException("Първо потвърди регистрацията от имейла си.");
        if (!encoder.matches(dto.getPassword(), user.getPasswordHash())) throw new IllegalArgumentException("Грешен имейл/username или парола.");
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());
    }

    public void sendPasswordReset(ForgotPasswordDto dto) {
        users.findByEmail(dto.getEmail()).ifPresent(user -> {
            PasswordResetToken token = new PasswordResetToken();
            token.setToken(UUID.randomUUID().toString()); token.setUser(user); token.setExpiresAt(LocalDateTime.now().plusHours(1)); token.setUsed(false);
            resetTokens.save(token);
            emailService.send(user.getEmail(), "Смяна на парола в Eventora", "Здравей!\n\nСмени паролата си от този линк:\n" + baseUrl + "/reset-password?token=" + token.getToken() + "\n\nЛинкът е валиден 1 час.");
        });
    }

    public void resetPassword(ResetPasswordDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) throw new IllegalArgumentException("Паролите не съвпадат.");
        PasswordResetToken token = resetTokens.findByToken(dto.getToken()).orElseThrow(() -> new IllegalArgumentException("Невалиден token."));
        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Линкът е невалиден или изтекъл.");
        User user = token.getUser(); user.setPasswordHash(encoder.encode(dto.getPassword())); users.save(user);
        token.setUsed(true); resetTokens.save(token);
    }
}
