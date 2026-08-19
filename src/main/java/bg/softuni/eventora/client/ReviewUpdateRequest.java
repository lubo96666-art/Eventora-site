package bg.softuni.eventora.client;

import java.util.UUID;

public record ReviewUpdateRequest(UUID authorId, int rating, String comment) {
}
