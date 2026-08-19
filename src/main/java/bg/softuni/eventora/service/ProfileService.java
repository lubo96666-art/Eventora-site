package bg.softuni.eventora.service;

import bg.softuni.eventora.dto.ProfileDto;
import bg.softuni.eventora.exception.BusinessRuleException;
import bg.softuni.eventora.exception.ResourceNotFoundException;
import bg.softuni.eventora.model.User;
import bg.softuni.eventora.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProfileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileService.class);
    private final UserRepository users;

    public ProfileService(UserRepository users) { this.users = users; }

    public User byId(UUID id) { return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("Потребителят не е намерен.")); }
    public List<User> allUsers() { return users.findAll(); }

    public ProfileDto toDto(User user) {
        ProfileDto dto = new ProfileDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }

    @Transactional
    public User update(UUID id, ProfileDto dto) {
        User user = byId(id);
        users.findByUsername(dto.getUsername()).filter(other -> !other.getId().equals(id))
                .ifPresent(other -> { throw new BusinessRuleException("Потребителското име е заето."); });
        users.findByEmail(dto.getEmail()).filter(other -> !other.getId().equals(id))
                .ifPresent(other -> { throw new BusinessRuleException("Имейлът е зает."); });
        user.setUsername(dto.getUsername().trim());
        user.setEmail(dto.getEmail().trim());
        LOGGER.info("Updated profile {}", id);
        return users.save(user);
    }

    @Transactional
    public void changeRole(UUID id, String role) {
        if (!List.of("USER", "VIP", "ADMIN").contains(role)) throw new BusinessRuleException("Невалидна роля.");
        User user = byId(id);
        user.setRole(role);
        users.save(user);
        LOGGER.info("Changed role for user {} to {}", id, role);
    }
}
