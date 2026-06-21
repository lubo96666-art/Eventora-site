package bg.softuni.eventora.controller;

import bg.softuni.eventora.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.UUID;
import bg.softuni.eventora.model.TicketOrder;

@Controller
public class TicketController {
    private final OrderService orderService;

    public TicketController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/my-tickets")
    public String myTickets(@RequestParam(required = false) String cancelled, HttpSession session, Model model) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/visualization?auth=login";
        }
        model.addAttribute("username", session.getAttribute("username"));
        List<TicketOrder> userOrders = orderService.ordersForUser((UUID) userId);
        model.addAttribute("orders", userOrders);
        model.addAttribute("ticketCount", orderService.ticketCountForUser((UUID) userId));
        model.addAttribute("orderCount", userOrders.size());
        model.addAttribute("totalSpent", orderService.totalSpentForUser((UUID) userId));
        if ("true".equals(cancelled)) {
            model.addAttribute("message", "Билетът беше отказан. Свободните места за събитието са възстановени.");
        }
        return "my-tickets";
    }

    @GetMapping("/my-tickets/{id}")
    public String ticketDetails(@PathVariable UUID id, HttpSession session, Model model) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/visualization?auth=login";
        }
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("order", orderService.orderForUser((UUID) userId, id));
        return "ticket-details";
    }


    @PostMapping("/my-tickets/{id}/cancel")
    public String cancelTicket(@PathVariable UUID id, HttpSession session, Model model) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/visualization?auth=login";
        }
        orderService.cancelOrder((UUID) userId, id);
        return "redirect:/my-tickets?cancelled=true";
    }
}
