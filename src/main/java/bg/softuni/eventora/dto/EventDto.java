package bg.softuni.eventora.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class EventDto {
    @NotBlank(message = "Въведи заглавие.")
    @Size(min = 3, max = 80, message = "Заглавието трябва да е между 3 и 80 символа.")
    private String title;

    @NotNull(message = "Избери зала.")
    private UUID venueId;

    @NotNull(message = "Въведи цена.")
    @DecimalMin(value = "1.00", message = "Цената трябва да е поне 1.00 €")
    @DecimalMax(value = "1000.00", message = "Цената трябва да е до 1000.00 €")
    private BigDecimal price;

    @Min(value = 1, message = "Свободните места трябва да са поне 1.")
    @Max(value = 5000, message = "Свободните места трябва да са до 5000.")
    private int availableSeats;

    @NotNull(message = "Избери дата и час.")
    @Future(message = "Събитието трябва да е в бъдещето.")
    private LocalDateTime startsAt;

    @NotBlank(message = "Избери категория.")
    private String category;

    @NotBlank(message = "Въведи описание.")
    @Size(min = 15, max = 900, message = "Описанието трябва да е между 15 и 900 символа.")
    private String description;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public UUID getVenueId() { return venueId; }
    public void setVenueId(UUID venueId) { this.venueId = venueId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public LocalDateTime getStartsAt() { return startsAt; }
    public void setStartsAt(LocalDateTime startsAt) { this.startsAt = startsAt; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
