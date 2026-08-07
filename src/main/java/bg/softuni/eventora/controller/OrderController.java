package bg.softuni.eventora.controller;

import bg.softuni.eventora.dto.CheckoutDto;
import bg.softuni.eventora.exception.BusinessRuleException;
import bg.softuni.eventora.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OrderController {
    private final OrderService orderService;
    private final PageController pageController;

    public OrderController(OrderService orderService, PageController pageController) {
        this.orderService = orderService;
        this.pageController = pageController;
    }

    @PostMapping("/checkout")
    public String checkout(@Valid @ModelAttribute CheckoutDto checkoutDto,
                           BindingResult bindingResult,
                           HttpSession session,
                           Model model) {
        if (bindingResult.hasErrors()) {
            pageController.addVisualizationModel(model, session, "none");
            return "visualization";
        }

        try {
            orderService.checkout((java.util.UUID) session.getAttribute("userId"),
                    checkoutDto.getEventIds(), checkoutDto.getQuantities(), checkoutDto.getPromoCode(),
                    checkoutDto.isUsePoints());
            return "redirect:/visualization?ordered=true";
        } catch (BusinessRuleException e) {
            pageController.addVisualizationModel(model, session, "none");
            model.addAttribute("error", e.getMessage());
            return "visualization";
        }
    }
}
