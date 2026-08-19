package bg.softuni.eventora.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "eventora-reviews", url = "${reviews.service.url}")
public interface ReviewsClient {
    @GetMapping("/api/reviews/event/{eventId}")
    List<ReviewResponse> byEvent(@PathVariable UUID eventId);
    @PostMapping("/api/reviews")
    ReviewResponse create(@RequestBody ReviewRequest request);
    @PutMapping("/api/reviews/{id}")
    ReviewResponse update(@PathVariable UUID id, @RequestBody ReviewUpdateRequest request);
    @DeleteMapping("/api/reviews/{id}")
    void delete(@PathVariable UUID id, @RequestParam UUID authorId);
}
