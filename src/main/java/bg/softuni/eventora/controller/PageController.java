package bg.softuni.eventora.controller;

import bg.softuni.eventora.dto.*;
import bg.softuni.eventora.service.EventService;
import bg.softuni.eventora.service.OrderService;
import bg.softuni.eventora.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import bg.softuni.eventora.model.TicketOrder;
import java.util.List;
import java.util.UUID;

@Controller
public class PageController {
    private final EventService eventService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    public PageController(EventService eventService, OrderService orderService, ReviewService reviewService) {
        this.eventService = eventService;
        this.orderService = orderService;
        this.reviewService = reviewService;
    }

    @GetMapping({"/", "/visualization"})
    public String visualization(@RequestParam(required = false) String auth, @RequestParam(required = false) String ordered, Model model, HttpSession session) {
        addVisualizationModel(model, session, auth == null ? "none" : auth);
        if ("true".equals(ordered)) model.addAttribute("message", "Поръчката е успешна. Билетите са запазени в профила ти.");
        return "visualization";
    }

    @GetMapping("/events/{id}")
    public String eventDetails(@PathVariable UUID id, Model model, HttpSession session) {
        model.addAttribute("event", eventService.getById(id));
        model.addAttribute("reviews", reviewService.forEvent(id));
        model.addAttribute("currentUserId", session.getAttribute("userId"));
        model.addAttribute("username", session.getAttribute("username"));
        return "event-details";
    }

    @GetMapping("/schedule")
    public String schedule(Model model) {
        model.addAttribute("events", eventService.allEvents());
        return "schedule";
    }

    @GetMapping("/about")
    public String about() { return "about"; }

    public void addVisualizationModel(Model model, HttpSession session, String authMode) {
        model.addAttribute("events", eventService.allEvents());
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));
        model.addAttribute("isAdmin", "ADMIN".equals(session.getAttribute("role")));
        model.addAttribute("authMode", authMode);
        Object userId = session.getAttribute("userId");
        int boughtTicketsCount = userId instanceof UUID ? orderService.ticketCountForUser((UUID) userId) : 0;
        int orderCount = userId instanceof UUID ? orderService.orderCountForUser((UUID) userId) : 0;
        int loyaltyPoints = userId instanceof UUID ? orderService.loyaltyPointsForUser((UUID) userId) : 0;
        List<TicketOrder> loyaltyHistory = userId instanceof UUID ? orderService.ordersForUser((UUID) userId) : List.of();
        model.addAttribute("boughtTicketsCount", boughtTicketsCount);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("loyaltyPoints", loyaltyPoints);
        model.addAttribute("loyaltyHistory", loyaltyHistory);
        if (!model.containsAttribute("registerDto")) model.addAttribute("registerDto", new RegisterDto());
        if (!model.containsAttribute("loginDto")) model.addAttribute("loginDto", new LoginDto());
        if (!model.containsAttribute("forgotPasswordDto")) model.addAttribute("forgotPasswordDto", new ForgotPasswordDto());
        if (!model.containsAttribute("resetPasswordDto")) model.addAttribute("resetPasswordDto", new ResetPasswordDto());
        if (!model.containsAttribute("verifyCodeDto")) model.addAttribute("verifyCodeDto", new VerifyCodeDto());
        if (!model.containsAttribute("checkoutDto")) model.addAttribute("checkoutDto", new CheckoutDto());
    }
}
