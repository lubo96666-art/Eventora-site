package bg.softuni.eventora.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String code;

    // Internal UUID kept for database compatibility with older versions. It is never shown on the site.
    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public UUID getId() { return id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
