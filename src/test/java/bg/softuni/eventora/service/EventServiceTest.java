package bg.softuni.eventora.service;

import bg.softuni.eventora.dto.EventDto;
import bg.softuni.eventora.exception.BusinessRuleException;
import bg.softuni.eventora.exception.ResourceNotFoundException;
import bg.softuni.eventora.model.Event;
import bg.softuni.eventora.model.Venue;
import bg.softuni.eventora.repository.EventRepository;
import bg.softuni.eventora.repository.TicketOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventServiceTest {

    private final EventRepository eventRepository = mock(EventRepository.class);
    private final TicketOrderRepository ticketOrderRepository = mock(TicketOrderRepository.class);
    private final VenueService venueService = mock(VenueService.class);
    private final EventService eventService = new EventService(eventRepository, ticketOrderRepository, venueService);
    private Venue venue;

    @BeforeEach
    void setUp() {
        venue = new Venue();
        venue.setName("Test Arena");
        venue.setCity("Sofia");
        venue.setAddress("Test address");
        venue.setCapacity(100);
    }

    @Test
    void createEventRejectsSeatsAboveVenueCapacity() {
        UUID venueId = UUID.randomUUID();
        EventDto dto = validDto(venueId);
        dto.setAvailableSeats(101);
        when(venueService.getById(venueId)).thenReturn(venue);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> eventService.createEvent(dto));

        assertEquals("Свободните места не може да надвишават капацитета на залата.",
                exception.getMessage());
    }

    @Test
    void decreaseSeatsRejectsUnavailableQuantity() {
        Event event = new Event();
        event.setAvailableSeats(2);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> eventService.decreaseSeats(event, 3));

        assertEquals("Няма достатъчно свободни места.", exception.getMessage());
    }

    @Test
    void decreaseSeatsPersistsNewAvailability() {
        Event event = new Event();
        event.setAvailableSeats(10);

        eventService.decreaseSeats(event, 3);

        assertEquals(7, event.getAvailableSeats());
        verify(eventRepository).save(event);
    }

    @Test
    void getByIdThrowsCustomExceptionForMissingEvent() {
        UUID eventId = UUID.randomUUID();
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.getById(eventId));
    }

    @Test
    void deleteEventRejectsEventWithPurchasedTickets() {
        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(ticketOrderRepository.existsByEvent_Id(eventId)).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> eventService.deleteEvent(eventId));

        assertEquals("Събитието не може да бъде изтрито, защото има закупени билети.",
                exception.getMessage());
    }

    private EventDto validDto(UUID venueId) {
        EventDto dto = new EventDto();
        dto.setTitle("Test Event");
        dto.setVenueId(venueId);
        dto.setPrice(new BigDecimal("25.00"));
        dto.setAvailableSeats(50);
        dto.setStartsAt(LocalDateTime.now().plusDays(5));
        dto.setCategory("Концерт");
        dto.setDescription("Valid event description for testing.");
        return dto;
    }
}
