package bg.softuni.eventora.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CheckoutDto {
    @NotEmpty(message = "Кошницата е празна.")
    private List<UUID> eventIds = new ArrayList<>();

    @NotEmpty(message = "Липсва количество за билетите.")
    private List<@Valid @Min(value = 1, message = "Минималното количество е 1.")
            @Max(value = 8, message = "Можеш да купиш до 8 билета от събитие.") Integer> quantities = new ArrayList<>();

    @Pattern(regexp = "(?i)^(|PROMO10|STUDENT15|VIP20)$", message = "Промо кодът не е валиден.")
    private String promoCode;

    private boolean usePoints;

    @AssertTrue(message = "Данните в кошницата са непълни.")
    public boolean isValidSelection() {
        return eventIds != null && quantities != null && !eventIds.isEmpty()
                && eventIds.size() == quantities.size();
    }

    public List<UUID> getEventIds() { return eventIds; }
    public void setEventIds(List<UUID> eventIds) { this.eventIds = eventIds; }
    public List<Integer> getQuantities() { return quantities; }
    public void setQuantities(List<Integer> quantities) { this.quantities = quantities; }
    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
    public boolean isUsePoints() { return usePoints; }
    public void setUsePoints(boolean usePoints) { this.usePoints = usePoints; }
}
