package bg.softuni.eventora.controller;

import bg.softuni.eventora.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class ReviewController {
    private final ReviewService service;

    public ReviewController(ReviewService service) { this.service = service; }

    @PostMapping("/events/{eventId}/reviews")
    public String create(@PathVariable UUID eventId, @RequestParam int rating, @RequestParam String comment, HttpSession session) {
        service.create(eventId, userId(session), session.getAttribute("username").toString(), rating, comment);
        return "redirect:/events/" + eventId + "?review=created";
    }

    @PostMapping("/events/{eventId}/reviews/{reviewId}/edit")
    public String update(@PathVariable UUID eventId, @PathVariable UUID reviewId, @RequestParam int rating,
                         @RequestParam String comment, HttpSession session) {
        service.update(reviewId, userId(session), rating, comment);
        return "redirect:/events/" + eventId + "?review=updated";
    }

    @PostMapping("/events/{eventId}/reviews/{reviewId}/delete")
    public String delete(@PathVariable UUID eventId, @PathVariable UUID reviewId, HttpSession session) {
        service.delete(reviewId, userId(session));
        return "redirect:/events/" + eventId + "?review=deleted";
    }

    private UUID userId(HttpSession session) { return (UUID) session.getAttribute("userId"); }
}
