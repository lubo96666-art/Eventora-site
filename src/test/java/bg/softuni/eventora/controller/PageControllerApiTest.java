package bg.softuni.eventora.controller;

import bg.softuni.eventora.client.ReviewsClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:eventora-api-test", "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
class PageControllerApiTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean ReviewsClient reviewsClient;

    @Test
    void homeIsPublic() throws Exception {
        mockMvc.perform(get("/visualization")).andExpect(status().isOk());
    }
}
