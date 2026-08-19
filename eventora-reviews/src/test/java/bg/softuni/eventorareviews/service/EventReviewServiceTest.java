package bg.softuni.eventorareviews.service;

import bg.softuni.eventorareviews.dto.ReviewRequest;
import bg.softuni.eventorareviews.model.EventReview;
import bg.softuni.eventorareviews.repository.EventReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventReviewServiceTest {
    @Mock EventReviewRepository repository;
    @InjectMocks EventReviewService service;

    @Test
    void createStoresValidReview() {
        UUID eventId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        when(repository.save(any(EventReview.class))).thenAnswer(invocation -> invocation.getArgument(0));
        assertEquals(5, service.create(new ReviewRequest(eventId, authorId, "Lyubo", 5, "Страхотно събитие")).rating());
    }
}
