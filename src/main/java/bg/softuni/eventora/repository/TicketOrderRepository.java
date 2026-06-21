package bg.softuni.eventora.repository;

import bg.softuni.eventora.model.TicketOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TicketOrderRepository extends JpaRepository<TicketOrder, UUID> {
    List<TicketOrder> findByUser_IdOrderByOrderedAtDesc(UUID userId);
}
