package bg.softuni.eventora.service;

import bg.softuni.eventora.model.Event;
import bg.softuni.eventora.model.TicketOrder;
import bg.softuni.eventora.model.User;
import bg.softuni.eventora.repository.TicketOrderRepository;
import bg.softuni.eventora.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final TicketOrderRepository orders;
    private final UserRepository users;
    private final EventService eventService;

    public OrderService(TicketOrderRepository orders, UserRepository users, EventService eventService) {
        this.orders = orders;
        this.users = users;
        this.eventService = eventService;
    }

    public List<TicketOrder> ordersForUser(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("Трябва да влезеш в профила си.");
        return orders.findByUser_IdOrderByOrderedAtDesc(userId);
    }




    public int ticketCountForUser(UUID userId) {
        return ordersForUser(userId).stream()
                .mapToInt(TicketOrder::getQuantity)
                .sum();
    }

    public int orderCountForUser(UUID userId) {
        return ordersForUser(userId).size();
    }

    public BigDecimal totalSpentForUser(UUID userId) {
        return ordersForUser(userId).stream()
                .map(TicketOrder::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public TicketOrder orderForUser(UUID userId, UUID orderId) {
        if (userId == null) throw new IllegalArgumentException("Трябва да влезеш в профила си.");
        return orders.findById(orderId)
                .filter(order -> order.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Билетът не е намерен."));
    }

    @Transactional
    public void checkout(UUID userId, List<UUID> eventIds, List<Integer> quantities, String promoCode) {
        if (userId == null) throw new IllegalArgumentException("Трябва да влезеш в профила си, за да купиш билети.");
        if (eventIds == null || quantities == null || eventIds.isEmpty() || eventIds.size() != quantities.size()) {
            throw new IllegalArgumentException("Кошницата е празна или невалидна.");
        }

        User user = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("Потребителят не е намерен."));
        int discountPercent = discountPercent(promoCode);
        String normalizedPromo = normalizePromoCode(promoCode);

        for (int i = 0; i < eventIds.size(); i++) {
            int quantity = quantities.get(i);
            Event event = eventService.getById(eventIds.get(i));
            eventService.decreaseSeats(event, quantity);

            TicketOrder order = new TicketOrder();
            order.setUser(user);
            order.setEvent(event);
            order.setQuantity(quantity);
            order.setPromoCode(normalizedPromo);
            order.setDiscountPercent(discountPercent);
            order.setOrderedAt(LocalDateTime.now());
            orders.save(order);
        }
    }


    @Transactional
    public void cancelOrder(UUID userId, UUID orderId) {
        TicketOrder order = orderForUser(userId, orderId);
        eventService.restoreSeats(order.getEvent(), order.getQuantity());
        orders.delete(order);
    }

    private int discountPercent(String promoCode) {
        String code = normalizePromoCode(promoCode);
        if (code == null) return 0;
        return switch (code) {
            case "PROMO10" -> 10;
            case "STUDENT15" -> 15;
            case "VIP20" -> 20;
            default -> throw new IllegalArgumentException("Промо кодът не е валиден.");
        };
    }

    private String normalizePromoCode(String promoCode) {
        if (promoCode == null || promoCode.isBlank()) return null;
        return promoCode.trim().toUpperCase();
    }
}
