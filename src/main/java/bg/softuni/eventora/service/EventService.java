package bg.softuni.eventora.service;

import bg.softuni.eventora.dto.EventDto;
import bg.softuni.eventora.model.Event;
import bg.softuni.eventora.model.Venue;
import bg.softuni.eventora.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventService implements CommandLineRunner {
    private final EventRepository eventRepository;
    private final VenueService venueService;

    public EventService(EventRepository eventRepository, VenueService venueService) {
        this.eventRepository = eventRepository;
        this.venueService = venueService;
    }

    public List<Event> allEvents() { return eventRepository.findAll(); }

    public Event getById(UUID id) {
        return eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Събитието не е намерено."));
    }

    public Event createEvent(EventDto dto) {
        Event event = new Event();
        copyDtoToEvent(dto, event);
        return eventRepository.save(event);
    }

    public Event updateEvent(UUID id, EventDto dto) {
        Event event = getById(id);
        copyDtoToEvent(dto, event);
        return eventRepository.save(event);
    }

    public void deleteEvent(UUID id) {
        Event event = getById(id);
        eventRepository.delete(event);
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
            throw new IllegalArgumentException("Свободните места не може да надвишават капацитета на залата.");
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

    public void decreaseSeats(Event event, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Избери поне 1 билет.");
        if (event.getAvailableSeats() < quantity) throw new IllegalArgumentException("Няма достатъчно свободни места.");
        event.setAvailableSeats(event.getAvailableSeats() - quantity);
        eventRepository.save(event);
    }

    public void restoreSeats(Event event, int quantity) {
        if (quantity < 1) return;
        event.setAvailableSeats(event.getAvailableSeats() + quantity);
        eventRepository.save(event);
    }

    @Override
    public void run(String... args) {
        if (eventRepository.count() < 22) {
            Venue arenaSofia = venueService.createIfMissing("Арена София", "София", "бул. Асен Йорданов 1", 12000);
            Venue ndk = venueService.createIfMissing("Зала 1 НДК", "София", "пл. България 1", 3300);
            Venue park = venueService.createIfMissing("Южен парк", "София", "Южен парк", 5000);
            Venue theatre = venueService.createIfMissing("Народен театър", "София", "ул. Дякон Игнатий 5", 780);
            Venue plovdiv = venueService.createIfMissing("Античен театър", "Пловдив", "Стария град", 3500);
            Venue interExpo = venueService.createIfMissing("Inter Expo Center", "София", "бул. Цариградско шосе 147", 2500);
            Venue stadium = venueService.createIfMissing("Национален стадион Васил Левски", "София", "бул. Евлоги и Христо Георгиеви 38", 43000);

            create("Lili Ivanova Live 2026", arenaSofia, "89.00", 320, LocalDateTime.now().plusDays(12).withHour(20).withMinute(0), "Концерт", "Голям концерт с хитове на живо, професионална сцена и запазени места.");
            create("Grafa & Band", ndk, "65.00", 180, LocalDateTime.now().plusDays(21).withHour(20).withMinute(30), "Концерт", "Вечер с популярни български песни, светлинно шоу и жива група.");
            create("Rock Legends Tribute", park, "38.00", 260, LocalDateTime.now().plusDays(30).withHour(19).withMinute(30), "Концерт", "Трибют концерт с класически рок хитове и открита сцена.");
            create("Jazz Under The Stars", plovdiv, "52.00", 140, LocalDateTime.now().plusDays(34).withHour(21).withMinute(0), "Концерт", "Лятна джаз вечер с гост музиканти и красива атмосфера.");
            create("Stand-up Comedy Night", ndk, "32.00", 95, LocalDateTime.now().plusDays(8).withHour(19).withMinute(30), "Комедия", "Вечер с популярни стендъп комедианти и жива публика.");
            create("Комеди Клуб: Най-доброто", ndk, "28.00", 70, LocalDateTime.now().plusDays(16).withHour(20).withMinute(0), "Комедия", "Селекция от най-силните шеги на сезона.");
            create("Импровизационен театър", theatre, "34.00", 85, LocalDateTime.now().plusDays(24).withHour(19).withMinute(0), "Комедия", "Интерактивно шоу, в което публиката задава темите.");
            create("Sofia Tech Summit", interExpo, "129.00", 110, LocalDateTime.now().plusDays(28).withHour(10).withMinute(0), "Конференция", "Практически лекции за Java, Spring и продуктови екипи.");
            create("Digital Marketing Forum", interExpo, "99.00", 160, LocalDateTime.now().plusDays(19).withHour(9).withMinute(30), "Конференция", "SEO, social media, branding и реални маркетинг казуси.");
            create("Startup Day Bulgaria", interExpo, "55.00", 200, LocalDateTime.now().plusDays(37).withHour(11).withMinute(0), "Конференция", "Среща за предприемачи, инвеститори и млади екипи.");
            create("Ромео и Жулиета", theatre, "46.00", 54, LocalDateTime.now().plusDays(14).withHour(19).withMinute(0), "Театър", "Класическа постановка с модерна сценография.");
            create("Хъшове", theatre, "58.00", 42, LocalDateTime.now().plusDays(23).withHour(19).withMinute(0), "Театър", "Една от най-обичаните български постановки.");
            create("Малкият принц", theatre, "24.00", 76, LocalDateTime.now().plusDays(11).withHour(18).withMinute(0), "Театър", "Семейна постановка, подходяща за деца и възрастни.");
            create("Левски - ЦСКА", stadium, "40.00", 850, LocalDateTime.now().plusDays(17).withHour(18).withMinute(45), "Спорт", "Голямото футболно дерби с електронни билети.");
            create("Баскетбол Финал", arenaSofia, "22.00", 430, LocalDateTime.now().plusDays(26).withHour(19).withMinute(0), "Спорт", "Финален мач с ограничени места близо до игрището.");
            create("Волейбол България - Италия", arenaSofia, "35.00", 520, LocalDateTime.now().plusDays(41).withHour(20).withMinute(0), "Спорт", "Международна волейболна среща с националния отбор.");
            create("Фестивал на храната", park, "18.00", 700, LocalDateTime.now().plusDays(10).withHour(12).withMinute(0), "Фестивал", "Street food, музика, дегустации и семейна зона.");
            create("Wine & Art Weekend", plovdiv, "44.00", 120, LocalDateTime.now().plusDays(32).withHour(17).withMinute(0), "Фестивал", "Дегустации на вино, изложби и акустична музика.");
            create("Burgas Summer Fest", park, "29.00", 650, LocalDateTime.now().plusDays(45).withHour(18).withMinute(30), "Фестивал", "Лятна фестивална вечер с DJ сцена и food зона.");
            create("Детска магия", theatre, "16.00", 100, LocalDateTime.now().plusDays(13).withHour(11).withMinute(0), "Детско", "Магическо шоу за деца с игри и награди.");
            create("Работилница по роботика", interExpo, "27.00", 35, LocalDateTime.now().plusDays(20).withHour(10).withMinute(30), "Детско", "Практическа работилница за деца с роботи и STEM задачи.");
            create("Приказен балет", theatre, "21.00", 88, LocalDateTime.now().plusDays(29).withHour(17).withMinute(0), "Детско", "Красив балетен спектакъл за цялото семейство.");
        }
    }

    private void create(String title, Venue venue, String price, int seats, LocalDateTime startsAt, String category, String description) {
        if (eventRepository.findAll().stream().anyMatch(e -> e.getTitle().equalsIgnoreCase(title))) {
            return;
        }
        Event event = new Event();
        event.setTitle(title);
        event.setVenue(venue);
        event.setLocation(venue.displayName());
        event.setPrice(new BigDecimal(price));
        event.setAvailableSeats(seats);
        event.setStartsAt(startsAt);
        event.setCategory(category);
        event.setDescription(description);
        eventRepository.save(event);
    }
}
