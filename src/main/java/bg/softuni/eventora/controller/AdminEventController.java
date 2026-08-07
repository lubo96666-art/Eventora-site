package bg.softuni.eventora.controller;

import bg.softuni.eventora.dto.EventDto;
import bg.softuni.eventora.exception.BusinessRuleException;
import bg.softuni.eventora.exception.EventoraAccessDeniedException;
import bg.softuni.eventora.model.Event;
import bg.softuni.eventora.service.EventService;
import bg.softuni.eventora.service.VenueService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Controller
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;
    private final VenueService venueService;

    public AdminEventController(EventService eventService, VenueService venueService) {
        this.eventService = eventService;
        this.venueService = venueService;
    }

    @GetMapping
    public String manage(@RequestParam(required = false) String action, Model model, HttpSession session) {
        requireAdmin(session);
        model.addAttribute("events", eventService.allEvents());
        model.addAttribute("username", session.getAttribute("username"));
        if ("created".equals(action)) model.addAttribute("message", "Събитието е създадено успешно.");
        if ("updated".equals(action)) model.addAttribute("message", "Промените са запазени успешно.");
        if ("deleted".equals(action)) model.addAttribute("message", "Събитието е изтрито успешно.");
        return "admin-events";
    }

    @GetMapping("/create")
    public String createForm(Model model, HttpSession session) {
        requireAdmin(session);
        model.addAttribute("eventDto", new EventDto());
        model.addAttribute("venues", venueService.allVenues());
        model.addAttribute("mode", "create");
        return "event-form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute EventDto eventDto, BindingResult bindingResult, Model model, HttpSession session) {
        requireAdmin(session);
        if (bindingResult.hasErrors()) {
            model.addAttribute("venues", venueService.allVenues());
            model.addAttribute("mode", "create");
            return "event-form";
        }
        try {
            eventService.createEvent(eventDto);
            return "redirect:/admin/events?action=created";
        } catch (BusinessRuleException exception) {
            model.addAttribute("venues", venueService.allVenues());
            model.addAttribute("mode", "create");
            model.addAttribute("error", exception.getMessage());
            return "event-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model, HttpSession session) {
        requireAdmin(session);
        Event event = eventService.getById(id);
        model.addAttribute("eventDto", eventService.toDto(event));
        model.addAttribute("venues", venueService.allVenues());
        model.addAttribute("eventId", id);
        model.addAttribute("mode", "edit");
        return "event-form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, @Valid @ModelAttribute EventDto eventDto, BindingResult bindingResult, Model model, HttpSession session) {
        requireAdmin(session);
        if (bindingResult.hasErrors()) {
            model.addAttribute("venues", venueService.allVenues());
            model.addAttribute("eventId", id);
            model.addAttribute("mode", "edit");
            return "event-form";
        }
        try {
            eventService.updateEvent(id, eventDto);
            return "redirect:/admin/events?action=updated";
        } catch (BusinessRuleException exception) {
            model.addAttribute("venues", venueService.allVenues());
            model.addAttribute("eventId", id);
            model.addAttribute("mode", "edit");
            model.addAttribute("error", exception.getMessage());
            return "event-form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, HttpSession session) {
        requireAdmin(session);
        eventService.deleteEvent(id);
        return "redirect:/admin/events?action=deleted";
    }

    private void requireAdmin(HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            throw new EventoraAccessDeniedException("Само администратор може да управлява събития.");
        }
    }
}
