package bg.softuni.eventora;

import bg.softuni.eventora.client.ReviewsClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:eventora-test", "spring.jpa.hibernate.ddl-auto=create-drop"})
class EventoraApplicationIntegrationTest {
    @MockitoBean ReviewsClient reviewsClient;

    @Test
    void contextLoads() {
    }
}
