package bg.softuni.eventora.service;

import bg.softuni.eventora.dto.EventDto;
import bg.softuni.eventora.exception.BusinessRuleException;
import bg.softuni.eventora.exception.ResourceNotFoundException;
import bg.softuni.eventora.model.Event;
import bg.softuni.eventora.model.Venue;
import bg.softuni.eventora.repository.EventRepository;
import bg.softuni.eventora.repository.TicketOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);
    private final EventRepository eventRepository;
    private final TicketOrderRepository ticketOrderRepository;
    private final VenueService venueService;

    public EventService(EventRepository eventRepository,
                        TicketOrderRepository ticketOrderRepository,
                        VenueService venueService) {
        this.eventRepository = eventRepository;
        this.ticketOrderRepository = ticketOrderRepository;
        this.venueService = venueService;
    }

    @Cacheable("events")
    public List<Event> allEvents() { return eventRepository.findAll(); }

    @Cacheable(value = "event", key = "#id")
    public Event getById(UUID id) {
        return eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Събитието не е намерено."));
    }

    @Transactional
    @CacheEvict(value = {"events", "event"}, allEntries = true)
    public Event createEvent(EventDto dto) {
        Event event = new Event();
        copyDtoToEvent(dto, event);
        Event saved = eventRepository.save(event);
        LOGGER.info("Created event {}", saved.getId());
        return saved;
    }

    @Transactional
    @CacheEvict(value = {"events", "event"}, allEntries = true)
    public Event updateEvent(UUID id, EventDto dto) {
        Event event = getById(id);
        copyDtoToEvent(dto, event);
        Event saved = eventRepository.save(event);
        LOGGER.info("Updated event {}", saved.getId());
        return saved;
    }

    @Transactional
    @CacheEvict(value = {"events", "event"}, allEntries = true)
    public void deleteEvent(UUID id) {
        Event event = getById(id);
        if (ticketOrderRepository.existsByEvent_Id(id)) {
            throw new BusinessRuleException("Събитието не може да бъде изтрито, защото има закупени билети.");
        }
        eventRepository.delete(event);
        LOGGER.info("Deleted event {}", id);
    }

    public EventDto toDto(Event event) {
        EventDto dto = new EventDto();
        dto.setTitle(event.getTitle());
        if (event.getVenue() != null) dto.setVenueId(event.getVenue().getId());
        dto.setPrice(event.getPrice());
        dto.setAvailableSeats(event.getAvailableSeats());
        dto.setStartsAt(event.getStartsAt());
        dto.setCategory(event.getCategory());
        dto.setDescription(event.getDescription());
        return dto;
    }

    private void copyDtoToEvent(EventDto dto, Event event) {
        Venue venue = venueService.getById(dto.getVenueId());
        if (dto.getAvailableSeats() > venue.getCapacity()) {
            throw new BusinessRuleException("Свободните места не може да надвишават капацитета на залата.");
        }
        event.setTitle(dto.getTitle().trim());
        event.setVenue(venue);
        event.setLocation(venue.displayName());
        event.setPrice(dto.getPrice());
        event.setAvailableSeats(dto.getAvailableSeats());
        event.setStartsAt(dto.getStartsAt());
        event.setCategory(dto.getCategory());
        event.setDescription(dto.getDescription().trim());
    }

    @Transactional
    @CacheEvict(value = {"events", "event"}, allEntries = true)
    public void decreaseSeats(Event event, int quantity) {
        if (quantity < 1) throw new BusinessRuleException("Избери поне 1 билет.");
        if (event.getAvailableSeats() < quantity) throw new BusinessRuleException("Няма достатъчно свободни места.");
        event.setAvailableSeats(event.getAvailableSeats() - quantity);
        eventRepository.save(event);
        LOGGER.info("Reserved {} seats for event {}", quantity, event.getId());
    }

    @Transactional
    @CacheEvict(value = {"events", "event"}, allEntries = true)
    public void restoreSeats(Event event, int quantity) {
        if (quantity < 1) return;
        event.setAvailableSeats(event.getAvailableSeats() + quantity);
        eventRepository.save(event);
        LOGGER.info("Restored {} seats for event {}", quantity, event.getId());
    }

}
