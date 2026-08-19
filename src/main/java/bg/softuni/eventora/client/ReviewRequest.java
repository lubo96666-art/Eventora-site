package bg.softuni.eventora.client;

import java.util.UUID;

public record ReviewRequest(UUID eventId, UUID authorId, String authorName, int rating, String comment) {
}
