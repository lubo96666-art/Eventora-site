package bg.softuni.eventora.repository;

import bg.softuni.eventora.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findFirstByCodeAndUser_Email(String code, String email);
    Optional<VerificationToken> findFirstByUser_Email(String email);
    void deleteByUser_Email(String email);
}
