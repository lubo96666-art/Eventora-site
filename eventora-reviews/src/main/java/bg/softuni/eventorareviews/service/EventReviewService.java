package bg.softuni.eventorareviews.service;

import bg.softuni.eventorareviews.dto.ReviewRequest;
import bg.softuni.eventorareviews.dto.ReviewResponse;
import bg.softuni.eventorareviews.dto.ReviewUpdateRequest;
import bg.softuni.eventorareviews.exception.ReviewNotFoundException;
import bg.softuni.eventorareviews.exception.ReviewRuleException;
import bg.softuni.eventorareviews.model.EventReview;
import bg.softuni.eventorareviews.repository.EventReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventReviewService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventReviewService.class);
    private final EventReviewRepository repository;

    public EventReviewService(EventReviewRepository repository) { this.repository = repository; }

    public List<ReviewResponse> reviewsForEvent(UUID eventId) {
        return repository.findByEventIdOrderByCreatedAtDesc(eventId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        if (repository.existsByEventIdAndAuthorId(request.eventId(), request.authorId())) {
            throw new ReviewRuleException("Вече си оставил оценка за това събитие.");
        }
        EventReview review = new EventReview();
        review.setEventId(request.eventId());
        review.setAuthorId(request.authorId());
        review.setAuthorName(request.authorName().trim());
        review.setRating(request.rating());
        review.setComment(request.comment().trim());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        EventReview saved = repository.save(review);
        LOGGER.info("Created review {} for event {}", saved.getId(), saved.getEventId());
        return toResponse(saved);
    }

    @Transactional
    public ReviewResponse update(UUID id, ReviewUpdateRequest request) {
        EventReview review = repository.findByIdAndAuthorId(id, request.authorId())
                .orElseThrow(() -> new ReviewNotFoundException("Оценката не е намерена или не е твоя."));
        review.setRating(request.rating());
        review.setComment(request.comment().trim());
        review.setUpdatedAt(LocalDateTime.now());
        LOGGER.info("Updated review {}", id);
        return toResponse(repository.save(review));
    }

    @Transactional
    public void delete(UUID id, UUID authorId) {
        EventReview review = repository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new ReviewNotFoundException("Оценката не е намерена или не е твоя."));
        repository.delete(review);
        LOGGER.info("Deleted review {}", id);
    }

    private ReviewResponse toResponse(EventReview review) {
        return new ReviewResponse(review.getId(), review.getEventId(), review.getAuthorId(), review.getAuthorName(),
                review.getRating(), review.getComment(), review.getCreatedAt(), review.getUpdatedAt());
    }
}
