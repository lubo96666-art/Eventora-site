package bg.softuni.eventorareviews.controller;

import bg.softuni.eventorareviews.dto.ReviewResponse;
import bg.softuni.eventorareviews.service.EventReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventReviewController.class)
class EventReviewApiTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean EventReviewService service;

    @Test
    void postCreatesReview() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.create(any())).thenReturn(new ReviewResponse(id, UUID.randomUUID(), UUID.randomUUID(), "Tester", 5, "Страхотно", LocalDateTime.now(), LocalDateTime.now()));
        mockMvc.perform(post("/api/reviews").contentType("application/json")
                .content("{\"eventId\":\"" + UUID.randomUUID() + "\",\"authorId\":\"" + UUID.randomUUID() + "\",\"authorName\":\"Tester\",\"rating\":5,\"comment\":\"Страхотно събитие\"}"))
                .andExpect(status().isCreated());
    }
}
