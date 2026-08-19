package bg.softuni.eventorareviews.integration;

import bg.softuni.eventorareviews.model.EventReview;
import bg.softuni.eventorareviews.repository.EventReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class EventReviewRepositoryIntegrationTest {
    @Autowired EventReviewRepository repository;

    @Test
    void findsReviewByEvent() {
        EventReview review = new EventReview();
        review.setEventId(UUID.randomUUID());
        review.setAuthorId(UUID.randomUUID());
        review.setAuthorName("Tester");
        review.setRating(4);
        review.setComment("Много добро събитие");
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        repository.save(review);
        assertTrue(repository.existsByEventIdAndAuthorId(review.getEventId(), review.getAuthorId()));
    }
}
