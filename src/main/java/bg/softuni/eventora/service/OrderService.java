package bg.softuni.eventora.service;

import bg.softuni.eventora.model.Event;
import bg.softuni.eventora.exception.BusinessRuleException;
import bg.softuni.eventora.exception.ResourceNotFoundException;
import bg.softuni.eventora.model.TicketOrder;
import bg.softuni.eventora.model.User;
import bg.softuni.eventora.repository.TicketOrderRepository;
import bg.softuni.eventora.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private static final int MAX_TICKETS_PER_EVENT = 8;
    private static final int LOYALTY_REWARD_COST = 100;
    private static final BigDecimal LOYALTY_DISCOUNT = BigDecimal.TEN;

    private final TicketOrderRepository orders;
    private final UserRepository users;
    private final EventService eventService;

    public OrderService(TicketOrderRepository orders, UserRepository users, EventService eventService) {
        this.orders = orders;
        this.users = users;
        this.eventService = eventService;
    }

    public List<TicketOrder> ordersForUser(UUID userId) {
        if (userId == null) {
            throw new BusinessRuleException("Трябва да влезеш в профила си.");
        }
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

    public int loyaltyPointsForUser(UUID userId) {
        if (userId == null) {
            throw new BusinessRuleException("Трябва да влезеш в профила си.");
        }
        User user = users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Потребителят не е намерен."));
        int earnedPoints = ordersForUser(userId).stream()
                .mapToInt(TicketOrder::getEarnedPoints)
                .sum();
        return Math.max(0, earnedPoints - user.getLoyaltyPointsSpent());
    }

    public TicketOrder orderForUser(UUID userId, UUID orderId) {
        if (userId == null) {
            throw new BusinessRuleException("Трябва да влезеш в профила си.");
        }
        return orders.findById(orderId)
                .filter(order -> order.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Билетът не е намерен."));
    }

    @Transactional
    public void checkout(UUID userId, List<UUID> eventIds, List<Integer> quantities, String promoCode) {
        checkout(userId, eventIds, quantities, promoCode, false);
    }

    @Transactional
    public void checkout(UUID userId, List<UUID> eventIds, List<Integer> quantities, String promoCode,
                         boolean usePoints) {
        if (userId == null) {
            throw new BusinessRuleException("Трябва да влезеш в профила си, за да купиш билети.");
        }
        if (eventIds == null || quantities == null || eventIds.isEmpty() || eventIds.size() != quantities.size()) {
            throw new BusinessRuleException("Кошницата е празна или невалидна.");
        }

        Map<UUID, Integer> requestedTickets = new LinkedHashMap<>();
        for (int i = 0; i < eventIds.size(); i++) {
            UUID eventId = eventIds.get(i);
            Integer quantity = quantities.get(i);
            if (eventId == null || quantity == null || quantity < 1) {
                throw new BusinessRuleException("Броят билети трябва да бъде между 1 и 8.");
            }
            int totalForEvent = requestedTickets.merge(eventId, quantity, Integer::sum);
            if (totalForEvent > MAX_TICKETS_PER_EVENT) {
                throw new BusinessRuleException("Можеш да поръчаш максимум 8 билета за едно събитие.");
            }
        }

        User user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Потребителят не е намерен."));
        int discountPercent = discountPercent(promoCode);
        String normalizedPromo = normalizePromoCode(promoCode);

        if (usePoints && loyaltyPointsForUser(userId) < LOYALTY_REWARD_COST) {
            throw new BusinessRuleException("Нямаш достатъчно точки за тази отстъпка.");
        }

        List<PendingOrder> pendingOrders = requestedTickets.entrySet().stream()
                .map(requested -> {
                    Event event = eventService.getById(requested.getKey());
                    BigDecimal priceAfterPromo = event.getPrice()
                            .multiply(BigDecimal.valueOf(requested.getValue()))
                            .multiply(BigDecimal.valueOf(100 - discountPercent))
                            .divide(BigDecimal.valueOf(100));
                    return new PendingOrder(event, requested.getValue(), priceAfterPromo);
                })
                .toList();

        BigDecimal basketTotal = pendingOrders.stream()
                .map(PendingOrder::priceAfterPromo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingLoyaltyDiscount = usePoints
                ? LOYALTY_DISCOUNT.min(basketTotal)
                : BigDecimal.ZERO;
        boolean pointsUsageRecorded = false;

        for (PendingOrder pending : pendingOrders) {
            int quantity = pending.quantity();
            Event event = pending.event();
            eventService.decreaseSeats(event, quantity);

            BigDecimal orderLoyaltyDiscount = remainingLoyaltyDiscount.min(pending.priceAfterPromo());
            remainingLoyaltyDiscount = remainingLoyaltyDiscount.subtract(orderLoyaltyDiscount);

            TicketOrder order = new TicketOrder();
            order.setUser(user);
            order.setEvent(event);
            order.setQuantity(quantity);
            order.setPromoCode(normalizedPromo);
            order.setDiscountPercent(discountPercent);
            order.setLoyaltyDiscount(orderLoyaltyDiscount);
            if (usePoints && !pointsUsageRecorded) {
                order.setLoyaltyPointsUsed(LOYALTY_REWARD_COST);
                pointsUsageRecorded = true;
            }
            order.setOrderedAt(LocalDateTime.now());
            orders.save(order);
        }

        if (usePoints) {
            user.setLoyaltyPointsSpent(user.getLoyaltyPointsSpent() + LOYALTY_REWARD_COST);
            users.save(user);
        }
        LOGGER.info("Completed checkout for user {} with {} event selections", userId, requestedTickets.size());
    }
    @Transactional
    public void cancelOrder(UUID userId, UUID orderId) {
        TicketOrder order = orderForUser(userId, orderId);
        eventService.restoreSeats(order.getEvent(), order.getQuantity());
        if (order.getLoyaltyPointsUsed() > 0) {
            User user = order.getUser();
            user.setLoyaltyPointsSpent(user.getLoyaltyPointsSpent() - order.getLoyaltyPointsUsed());
            users.save(user);
        }
        orders.delete(order);
        LOGGER.info("Cancelled order {} for user {}", orderId, userId);
    }

    private int discountPercent(String promoCode) {
        String code = normalizePromoCode(promoCode);
        if (code == null) {
            return 0;
        }
        return switch (code) {
            case "PROMO10" -> 10;
            case "STUDENT15" -> 15;
            case "VIP20" -> 20;
            default -> throw new BusinessRuleException("Промо кодът не е валиден.");
        };
    }

    private String normalizePromoCode(String promoCode) {
        if (promoCode == null || promoCode.isBlank()) {
            return null;
        }
        return promoCode.trim().toUpperCase();
    }

    private record PendingOrder(Event event, int quantity, BigDecimal priceAfterPromo) {
    }
}
