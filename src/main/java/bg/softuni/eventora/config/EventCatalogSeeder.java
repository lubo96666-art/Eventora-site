package bg.softuni.eventora.config;

import bg.softuni.eventora.dto.EventDto;
import bg.softuni.eventora.model.Venue;
import bg.softuni.eventora.repository.EventRepository;
import bg.softuni.eventora.service.EventService;
import bg.softuni.eventora.service.VenueService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class EventCatalogSeeder implements CommandLineRunner {
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final VenueService venueService;

    public EventCatalogSeeder(EventRepository eventRepository,
                              EventService eventService,
                              VenueService venueService) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.venueService = venueService;
    }

    @Override
    public void run(String... args) {
        Venue arena = venueService.createIfMissing("Арена София", "София", "бул. Асен Йорданов 1", 12000);
        Venue ndk = venueService.createIfMissing("Зала 1 НДК", "София", "пл. България 1", 3300);
        Venue park = venueService.createIfMissing("Южен парк", "София", "Южен парк", 5000);
        Venue theatre = venueService.createIfMissing("Народен театър", "София", "ул. Дякон Игнатий 5", 780);
        Venue plovdiv = venueService.createIfMissing("Античен театър", "Пловдив", "Стария град", 3500);
        Venue expo = venueService.createIfMissing("Inter Expo Center", "София", "бул. Цариградско шосе 147", 2500);
        Venue stadium = venueService.createIfMissing("Национален стадион Васил Левски", "София", "бул. Евлоги и Христо Георгиеви 38", 43000);

        List<EventSeed> catalog = List.of(
                new EventSeed("Lili Ivanova Live 2026", arena, "89.00", 320, 12, 20, 0, "Концерт", "Голям концерт с хитове на живо, професионална сцена и запазени места."),
                new EventSeed("Grafa & Band", ndk, "65.00", 180, 21, 20, 30, "Концерт", "Вечер с популярни български песни, светлинно шоу и жива група."),
                new EventSeed("Rock Legends Tribute", park, "38.00", 260, 30, 19, 30, "Концерт", "Трибют концерт с класически рок хитове и открита сцена."),
                new EventSeed("Jazz Under The Stars", plovdiv, "52.00", 140, 34, 21, 0, "Концерт", "Лятна джаз вечер с гост музиканти и красива атмосфера."),
                new EventSeed("Stand-up Comedy Night", ndk, "32.00", 95, 8, 19, 30, "Комедия", "Вечер с популярни стендъп комедианти и жива публика."),
                new EventSeed("Комеди Клуб: Най-доброто", ndk, "28.00", 70, 16, 20, 0, "Комедия", "Селекция от най-силните шеги на сезона."),
                new EventSeed("Импровизационен театър", theatre, "34.00", 85, 24, 19, 0, "Комедия", "Интерактивно шоу, в което публиката задава темите."),
                new EventSeed("Sofia Tech Summit", expo, "129.00", 110, 28, 10, 0, "Конференция", "Практически лекции за Java, Spring и продуктови екипи."),
                new EventSeed("Digital Marketing Forum", expo, "99.00", 160, 19, 9, 30, "Конференция", "SEO, social media, branding и реални маркетинг казуси."),
                new EventSeed("Startup Day Bulgaria", expo, "55.00", 200, 37, 11, 0, "Конференция", "Среща за предприемачи, инвеститори и млади екипи."),
                new EventSeed("Ромео и Жулиета", theatre, "46.00", 54, 14, 19, 0, "Театър", "Класическа постановка с модерна сценография."),
                new EventSeed("Хъшове", theatre, "58.00", 42, 23, 19, 0, "Театър", "Една от най-обичаните български постановки."),
                new EventSeed("Малкият принц", theatre, "24.00", 76, 11, 18, 0, "Театър", "Семейна постановка, подходяща за деца и възрастни."),
                new EventSeed("Левски - ЦСКА", stadium, "40.00", 850, 17, 18, 45, "Спорт", "Голямото футболно дерби с електронни билети."),
                new EventSeed("Баскетбол Финал", arena, "22.00", 430, 26, 19, 0, "Спорт", "Финален мач с ограничени места близо до игрището."),
                new EventSeed("Волейбол България - Италия", arena, "35.00", 520, 41, 20, 0, "Спорт", "Международна волейболна среща с националния отбор."),
                new EventSeed("Фестивал на храната", park, "18.00", 700, 10, 12, 0, "Фестивал", "Street food, музика, дегустации и семейна зона."),
                new EventSeed("Wine & Art Weekend", plovdiv, "44.00", 120, 32, 17, 0, "Фестивал", "Дегустации на вино, изложби и акустична музика."),
                new EventSeed("Burgas Summer Fest", park, "29.00", 650, 45, 18, 30, "Фестивал", "Лятна фестивална вечер с DJ сцена и food зона."),
                new EventSeed("Детска магия", theatre, "16.00", 100, 13, 11, 0, "Детско", "Магическо шоу за деца с игри и награди."),
                new EventSeed("Работилница по роботика", expo, "27.00", 35, 20, 10, 30, "Детско", "Практическа работилница за деца с роботи и STEM задачи."),
                new EventSeed("Приказен балет", theatre, "21.00", 88, 29, 17, 0, "Детско", "Красив балетен спектакъл за цялото семейство.")
        );

        Set<String> existingTitles = new HashSet<>();
        eventRepository.findAll().forEach(event -> existingTitles.add(event.getTitle().toLowerCase()));
        catalog.stream()
                .filter(seed -> !existingTitles.contains(seed.title().toLowerCase()))
                .forEach(this::create);
    }

    private void create(EventSeed seed) {
        EventDto dto = new EventDto();
        dto.setTitle(seed.title());
        dto.setVenueId(seed.venue().getId());
        dto.setPrice(new BigDecimal(seed.price()));
        dto.setAvailableSeats(seed.seats());
        dto.setStartsAt(LocalDateTime.now().plusDays(seed.daysFromNow()).withHour(seed.hour()).withMinute(seed.minute()).withSecond(0).withNano(0));
        dto.setCategory(seed.category());
        dto.setDescription(seed.description());
        eventService.createEvent(dto);
    }

    private record EventSeed(String title, Venue venue, String price, int seats,
                             int daysFromNow, int hour, int minute,
                             String category, String description) {
    }
}
