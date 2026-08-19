package bg.softuni.eventora.service;

import bg.softuni.eventora.client.ReviewRequest;
import bg.softuni.eventora.client.ReviewResponse;
import bg.softuni.eventora.client.ReviewUpdateRequest;
import bg.softuni.eventora.client.ReviewsClient;
import bg.softuni.eventora.exception.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewsClient client;

    public ReviewService(ReviewsClient client) { this.client = client; }

    public List<ReviewResponse> forEvent(UUID eventId) {
        try { return client.byEvent(eventId); } catch (RuntimeException exception) { return List.of(); }
    }

    public void create(UUID eventId, UUID authorId, String authorName, int rating, String comment) {
        validate(rating, comment);
        client.create(new ReviewRequest(eventId, authorId, authorName, rating, comment));
        LOGGER.info("Review creation requested for event {}", eventId);
    }

    public void update(UUID reviewId, UUID authorId, int rating, String comment) {
        validate(rating, comment);
        client.update(reviewId, new ReviewUpdateRequest(authorId, rating, comment));
        LOGGER.info("Review update requested for {}", reviewId);
    }

    public void delete(UUID reviewId, UUID authorId) {
        client.delete(reviewId, authorId);
        LOGGER.info("Review deletion requested for {}", reviewId);
    }

    private void validate(int rating, String comment) {
        if (rating < 1 || rating > 5) throw new BusinessRuleException("Оценката трябва да е между 1 и 5.");
        if (comment == null || comment.trim().length() < 5 || comment.length() > 500) {
            throw new BusinessRuleException("Коментарът трябва да е между 5 и 500 символа.");
        }
    }
}
