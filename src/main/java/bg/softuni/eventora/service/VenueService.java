package bg.softuni.eventora.service;

import bg.softuni.eventora.model.Venue;
import bg.softuni.eventora.repository.VenueRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class VenueService {
    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public List<Venue> allVenues() { return venueRepository.findAll(); }

    public Venue getById(UUID id) {
        return venueRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Залата не е намерена."));
    }

    public Venue createIfMissing(String name, String city, String address, int capacity) {
        return venueRepository.findByName(name).orElseGet(() -> {
            Venue venue = new Venue();
            venue.setName(name);
            venue.setCity(city);
            venue.setAddress(address);
            venue.setCapacity(capacity);
            return venueRepository.save(venue);
        });
    }
}
