package bg.softuni.eventora.controller;

import bg.softuni.eventora.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
public class OrderController {
    private final OrderService orderService;
    private final PageController pageController;

    public OrderController(OrderService orderService, PageController pageController) {
        this.orderService = orderService;
        this.pageController = pageController;
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam(required = false) List<UUID> eventIds,
                           @RequestParam(required = false) List<Integer> quantities,
                           @RequestParam(required = false) String promoCode,
                           HttpSession session,
                           Model model) {
        Object rawUserId = session.getAttribute("userId");
        if (rawUserId == null) {
            pageController.addVisualizationModel(model, session, "login");
            model.addAttribute("error", "Влез в профила си, за да завършиш поръчката.");
            return "visualization";
        }

        try {
            orderService.checkout((UUID) rawUserId, eventIds, quantities, promoCode);
            return "redirect:/visualization?ordered=true";
        } catch (Exception e) {
            pageController.addVisualizationModel(model, session, "none");
            model.addAttribute("error", e.getMessage());
            return "visualization";
        }
    }
}
