package bg.softuni.eventora.service;

import bg.softuni.eventora.exception.BusinessRuleException;
import bg.softuni.eventora.model.Event;
import bg.softuni.eventora.model.TicketOrder;
import bg.softuni.eventora.model.User;
import bg.softuni.eventora.repository.TicketOrderRepository;
import bg.softuni.eventora.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    private final TicketOrderRepository orders = mock(TicketOrderRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final EventService eventService = mock(EventService.class);
    private final OrderService orderService = new OrderService(orders, users, eventService);

    @Test
    void checkoutRejectsMoreThanEightTicketsForOneEvent() {
        UUID eventId = UUID.randomUUID();

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> orderService.checkout(UUID.randomUUID(), List.of(eventId), List.of(9), null));

        assertEquals("Можеш да поръчаш максимум 8 билета за едно събитие.", exception.getMessage());
        verifyNoInteractions(users, eventService, orders);
    }

    @Test
    void checkoutRejectsAnonymousUser() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> orderService.checkout(null, List.of(UUID.randomUUID()), List.of(1), null));

        assertEquals("Трябва да влезеш в профила си, за да купиш билети.", exception.getMessage());
        verifyNoInteractions(users, eventService, orders);
    }

    @Test
    void checkoutRejectsEmptyCart() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> orderService.checkout(UUID.randomUUID(), List.of(), List.of(), null));

        assertEquals("Кошницата е празна или невалидна.", exception.getMessage());
        verifyNoInteractions(users, eventService, orders);
    }

    @Test
    void checkoutRejectsDuplicatedLinesWhoseCombinedQuantityExceedsEight() {
        UUID eventId = UUID.randomUUID();

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> orderService.checkout(UUID.randomUUID(),
                        List.of(eventId, eventId), List.of(5, 4), null));

        assertEquals("Можеш да поръчаш максимум 8 билета за едно събитие.", exception.getMessage());
        verifyNoInteractions(users, eventService, orders);
    }

    @Test
    void loyaltyPointsAreCalculatedFromEachUsersOwnPurchases() {
        UUID firstUserId = UUID.randomUUID();
        UUID secondUserId = UUID.randomUUID();
        User firstUser = new User();
        firstUser.setLoyaltyPointsSpent(20);
        User secondUser = new User();

        Event event = new Event();
        event.setPrice(new BigDecimal("100.00"));
        TicketOrder firstUsersOrder = new TicketOrder();
        firstUsersOrder.setEvent(event);
        firstUsersOrder.setQuantity(2);

        when(users.findById(firstUserId)).thenReturn(Optional.of(firstUser));
        when(users.findById(secondUserId)).thenReturn(Optional.of(secondUser));
        when(orders.findByUser_IdOrderByOrderedAtDesc(firstUserId)).thenReturn(List.of(firstUsersOrder));
        when(orders.findByUser_IdOrderByOrderedAtDesc(secondUserId)).thenReturn(List.of());

        assertEquals(180, orderService.loyaltyPointsForUser(firstUserId));
        assertEquals(0, orderService.loyaltyPointsForUser(secondUserId));
    }
}
