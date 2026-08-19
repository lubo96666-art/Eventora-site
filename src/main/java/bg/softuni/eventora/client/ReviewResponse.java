package bg.softuni.eventora.client;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewResponse(UUID id, UUID eventId, UUID authorId, String authorName, int rating,
                             String comment, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
