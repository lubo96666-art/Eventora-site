package bg.softuni.eventora.config;

import bg.softuni.eventora.model.User;
import bg.softuni.eventora.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DataSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        createUserIfMissing("ivan", "ivan@example.com", "123456", "USER");
        createUserIfMissing("maria", "maria@example.com", "123456", "USER");
        createUserIfMissing("vip", "vip@example.com", "vip123", "VIP");
        createUserIfMissing("admin", "admin@example.com", "admin123", "ADMIN");
    }

    private void createUserIfMissing(String username, String email, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(password));
        user.setEnabled(true);
        user.setRole(role);
        userRepository.save(user);
    }
}
