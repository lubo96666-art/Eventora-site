package bg.softuni.eventora.repository;

import bg.softuni.eventora.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface EventRepository extends JpaRepository<Event, UUID> {
}
