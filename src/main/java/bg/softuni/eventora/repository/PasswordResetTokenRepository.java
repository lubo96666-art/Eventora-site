package bg.softuni.eventora.repository;

import bg.softuni.eventora.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByToken(String token);
}
