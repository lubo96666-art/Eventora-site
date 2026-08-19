package bg.softuni.eventorareviews.repository;

import bg.softuni.eventorareviews.model.EventReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventReviewRepository extends JpaRepository<EventReview, UUID> {
    List<EventReview> findByEventIdOrderByCreatedAtDesc(UUID eventId);
    Optional<EventReview> findByIdAndAuthorId(UUID id, UUID authorId);
    boolean existsByEventIdAndAuthorId(UUID eventId, UUID authorId);
}
