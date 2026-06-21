package bg.softuni.eventora.controller;

import bg.softuni.eventora.dto.*;
import bg.softuni.eventora.service.EventService;
import bg.softuni.eventora.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.UUID;

@Controller
public class PageController {
    private final EventService eventService;
    private final OrderService orderService;

    public PageController(EventService eventService, OrderService orderService) {
        this.eventService = eventService;
        this.orderService = orderService;
    }

    @GetMapping({"/", "/visualization"})
    public String visualization(@RequestParam(required = false) String auth, @RequestParam(required = false) String ordered, Model model, HttpSession session) {
        addVisualizationModel(model, session, auth == null ? "none" : auth);
        if ("true".equals(ordered)) model.addAttribute("message", "Поръчката е успешна. Билетите са запазени в профила ти.");
        return "visualization";
    }

    public void addVisualizationModel(Model model, HttpSession session, String authMode) {
        model.addAttribute("events", eventService.allEvents());
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));
        model.addAttribute("isAdmin", "ADMIN".equals(session.getAttribute("role")));
        model.addAttribute("authMode", authMode);
        Object userId = session.getAttribute("userId");
        int boughtTicketsCount = userId instanceof UUID ? orderService.ticketCountForUser((UUID) userId) : 0;
        int orderCount = userId instanceof UUID ? orderService.orderCountForUser((UUID) userId) : 0;
        model.addAttribute("boughtTicketsCount", boughtTicketsCount);
        model.addAttribute("orderCount", orderCount);
        if (!model.containsAttribute("registerDto")) model.addAttribute("registerDto", new RegisterDto());
        if (!model.containsAttribute("loginDto")) model.addAttribute("loginDto", new LoginDto());
        if (!model.containsAttribute("forgotPasswordDto")) model.addAttribute("forgotPasswordDto", new ForgotPasswordDto());
        if (!model.containsAttribute("resetPasswordDto")) model.addAttribute("resetPasswordDto", new ResetPasswordDto());
        if (!model.containsAttribute("verifyCodeDto")) model.addAttribute("verifyCodeDto", new VerifyCodeDto());
    }
}
