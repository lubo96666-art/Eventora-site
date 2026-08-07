package bg.softuni.eventora.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
public class TicketOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Event event;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    private String promoCode;
    private int discountPercent;

    @Column(precision = 10, scale = 2)
    private BigDecimal loyaltyDiscount = BigDecimal.ZERO;

    private Integer loyaltyPointsUsed = 0;

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return event == null
                ? BigDecimal.ZERO
                : event.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getFinalPrice() {
        BigDecimal priceAfterPromo = getTotalPrice()
                .multiply(BigDecimal.valueOf(100 - discountPercent))
                .divide(BigDecimal.valueOf(100));
        return priceAfterPromo.subtract(getLoyaltyDiscount()).max(BigDecimal.ZERO);
    }

    public int getEarnedPoints() {
        return getFinalPrice().setScale(0, RoundingMode.DOWN).intValue();
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getLoyaltyDiscount() {
        return loyaltyDiscount == null ? BigDecimal.ZERO : loyaltyDiscount;
    }

    public void setLoyaltyDiscount(BigDecimal loyaltyDiscount) {
        this.loyaltyDiscount = loyaltyDiscount == null
                ? BigDecimal.ZERO
                : loyaltyDiscount.max(BigDecimal.ZERO);
    }

    public int getLoyaltyPointsUsed() {
        return loyaltyPointsUsed == null ? 0 : loyaltyPointsUsed;
    }

    public void setLoyaltyPointsUsed(int loyaltyPointsUsed) {
        this.loyaltyPointsUsed = Math.max(0, loyaltyPointsUsed);
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }
}
