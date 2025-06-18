package de.ckollmeier.burgerexpress.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO;

import de.ckollmeier.burgerexpress.backend.dto.OrderInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderItemInputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CustomerSessionController")
@WithMockUser
class CustomerSessionControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DishRepository dishRepository;

    @Nested
    @DisplayName("GET /api/customer-sessions")
    class GetCustomerSessionTests {

        @Test
        @DisplayName("returns null when no session exists")
        void should_returnNoContent_whenNoSessionExists() throws Exception {
            // Given
            MockHttpSession session = new MockHttpSession();

            // When & Then
            mockMvc.perform(get("/api/customer-sessions")
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("returns the existing customer session")
        void should_returnExistingCustomerSession_whenOneExists() throws Exception {
            // Given
            MvcResult result = mockMvc.perform(post("/api/customer-sessions")).andReturn();
            String sessionCookie = result.getResponse().getHeader("Set-Cookie");
            Assertions.assertNotNull(sessionCookie);
            Cookie cookie = new Cookie("SESSION", sessionCookie.substring(8, sessionCookie.indexOf(";")));

            CustomerSessionDTO expectedDTO = objectMapper.readValue(result.getResponse().getContentAsString(), CustomerSessionDTO.class);

            // When & Then
            mockMvc.perform(get("/api/customer-sessions")
                            .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.createdAt").value(expectedDTO.createdAt()))
                    .andExpect(jsonPath("$.expiresAt").value(expectedDTO.expiresAt()))
                    .andExpect(jsonPath("$.expiresInSeconds").isNumber())
                    .andExpect(jsonPath("$.expired").value(false));

        }
    }

    @Nested
    @DisplayName("POST /api/customer-sessions")
    class CreateCustomerSessionTests {

        @Test
        @DisplayName("creates a new customer session")
        void should_createNewCustomerSession() throws Exception {

            // When & Then
            mockMvc.perform(post("/api/customer-sessions")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                    .andExpect(jsonPath("$.expiresInSeconds").isNumber())
                    .andExpect(jsonPath("$.expired").value(false));
        }
    }

    @Nested
    @DisplayName("PUT /api/customer-sessions")
    class RenewCustomerSessionTests {

        @Test
        @DisplayName("renews an existing customer session")
        void should_renewExistingCustomerSession() throws Exception {
            // Given
            MvcResult result = mockMvc.perform(post("/api/customer-sessions")).andReturn();
            String sessionCookie = result.getResponse().getHeader("Set-Cookie");
            Assertions.assertNotNull(sessionCookie);
            Cookie cookie = new Cookie("SESSION", sessionCookie.substring(8, sessionCookie.indexOf(";")));

            CustomerSessionDTO initialDTO = objectMapper.readValue(result.getResponse().getContentAsString(), CustomerSessionDTO.class);

            // When & Then
            MvcResult renewResult = mockMvc.perform(put("/api/customer-sessions")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.createdAt").value(initialDTO.createdAt()))
                    .andExpect(jsonPath("$.expiresInSeconds").value(299))
                    .andExpect(jsonPath("$.expired").value(false))
                    .andReturn();

            CustomerSessionDTO renewedDTO = objectMapper.readValue(renewResult.getResponse().getContentAsString(), CustomerSessionDTO.class);
            assertThat(renewedDTO.createdAt()).isEqualTo(initialDTO.createdAt());
            assertThat(renewedDTO.expiresInSeconds()).isGreaterThanOrEqualTo(initialDTO.expiresInSeconds());
            assertThat(renewedDTO.expiresAt()).isGreaterThanOrEqualTo(initialDTO.expiresAt());
        }

        @Test
        @DisplayName("returns 400 when no session exists")
        void should_return404_whenNoSessionExists() throws Exception {
            // When & Then
            mockMvc.perform(put("/api/customer-sessions")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("IllegalStateException"))
                    .andExpect(jsonPath("$.message").value("No customer session found"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/customer-sessions")
    class RemoveCustomerSessionTests {

        @Test
        @DisplayName("removes the customer session")
        void should_removeCustomerSession() throws Exception {
            // Given
            MvcResult result = mockMvc.perform(post("/api/customer-sessions")).andReturn();
            String sessionCookie = result.getResponse().getHeader("Set-Cookie");
            Assertions.assertNotNull(sessionCookie);
            Cookie cookie = new Cookie("SESSION", sessionCookie.substring(8, sessionCookie.indexOf(";")));

            // First create a session
            mockMvc.perform(post("/api/customer-sessions")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());

            // When & Then
            mockMvc.perform(delete("/api/customer-sessions")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verify session is removed by trying to renew it
            mockMvc.perform(put("/api/customer-sessions")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/customer-sessions")
    class StoreOrderTests {

        @Test
        @DisplayName("returns 404 when no session exists")
        void should_return404_whenNoSessionExists() throws Exception {
            // Given
            MockHttpSession session = new MockHttpSession();
            String orderJson = """
                    {
                        "id": "order-1",
                        "items": [
                            {
                                "item": "item-1",
                                "amount": 2
                            }
                        ]
                    }
                    """;

            // When & Then
            mockMvc.perform(patch("/api/customer-sessions")
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(orderJson))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("stores order in session and returns updated session")
        void should_storeOrderInSession_andReturnUpdatedSession() throws Exception {
            // Given
            Dish testDish = Dish.builder()
                    .id("test-dish")
                    .name("Test Dish")
                    .price(new java.math.BigDecimal("9.99"))
                    .type(DishType.MAIN)
                    .build();

            dishRepository.save(testDish);

            OrderInputDTO order = new OrderInputDTO(
                    null,
                    List.of(new OrderItemInputDTO(
                            null,
                            testDish.getId(),
                            1
                    ))
            );

            // First create a session and get the session cookie
            MvcResult initialResult = mockMvc.perform(post("/api/customer-sessions")).andReturn();
            String sessionCookie = initialResult.getResponse().getHeader("Set-Cookie");
            Assertions.assertNotNull(sessionCookie);
            Cookie cookie = new Cookie("SESSION", sessionCookie.substring(8, sessionCookie.indexOf(";")));

            // When & Then
            mockMvc.perform(patch("/api/customer-sessions")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(order)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.expiresAt").exists())
                    .andExpect(jsonPath("$.expiresInSeconds").value(299))
                    .andExpect(jsonPath("$.expired").value(false))
                    .andExpect(jsonPath("$.order.id").exists())
                    .andExpect(jsonPath("$.order.items").isArray())
                    .andExpect(jsonPath("$.order.items[0].item.id").value(testDish.getId()))
            ;

        }
    }
}
