package bg.softuni.eventora.repository;

import bg.softuni.eventora.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface VenueRepository extends JpaRepository<Venue, UUID> {
    Optional<Venue> findByName(String name);
}
