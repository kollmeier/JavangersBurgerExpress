package de.ckollmeier.burgerexpress.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ckollmeier.burgerexpress.backend.dto.OrderInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderItemInputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.repository.OrderRepository;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DishRepository dishRepository;

    private Dish testDish;

    @BeforeEach
    void setUp() {
        // Clean up repositories
        orderRepository.deleteAll();
        dishRepository.deleteAll();

        // Create test dish
        testDish = dishRepository.save(Dish.builder()
                .name("Test Dish")
                .price(BigDecimal.valueOf(9.99))
                .type(DishType.MAIN)
                .build());
    }

    @AfterEach
    void tearDown() {
        // Clean up repositories
        orderRepository.deleteAll();
        dishRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /api/orders")
    class PlaceOrder {

        @Test
        @DisplayName("should place an order with status CHECKOUT")
        void shouldPlaceOrderWithStatusCheckout() throws Exception {
            // Given
            long initialOrderCount = orderRepository.count();

            OrderInputDTO orderInputDTO = new OrderInputDTO(
                    null,
                    List.of(new OrderItemInputDTO(null, testDish.getId(), 2))
            );
            MvcResult initialResult = mockMvc.perform(post("/api/customer-sessions")).andReturn();
            String sessionCookie = initialResult.getResponse().getHeader("Set-Cookie");
            Assertions.assertNotNull(sessionCookie);
            Cookie cookie = new Cookie("SESSION", sessionCookie.substring(8, sessionCookie.indexOf(";")));

            MvcResult orderInSessionResult = mockMvc.perform(patch("/api/customer-sessions")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderInputDTO))
            ).andReturn();

            String orderId = objectMapper.readTree(orderInSessionResult.getResponse().getContentAsString()).get("order").get("id").asText();

            // When
            MvcResult result = mockMvc.perform(post("/api/orders")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.order").exists())
                    .andReturn();

            // Then
            // Extract the order ID from the response
            String responseBody = result.getResponse().getContentAsString();
            String returnedOrderId = objectMapper.readTree(responseBody).get("order").get("id").asText ();
            // Verify that the returned order ID matches the one we provided
            assertThat(returnedOrderId).isEqualTo(orderId);

            // Verify that an order was created
            assertThat(orderRepository.count()).isEqualTo(initialOrderCount + 1);
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            assertThat(orderOptional).isPresent();

            Order order = orderOptional.get();
            assertThat(order.getItems())
                    .hasSize(1)
                    .extracting("item.id", "item.name", "item.price", "amount", "price")
                    .containsExactlyInAnyOrder(tuple(testDish.getId(), testDish.getName(), testDish.getPrice(), 2, testDish.getPrice().multiply(BigDecimal.valueOf(2))));
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CHECKOUT);

        }

        @Test
        @DisplayName("should return 400 when an error occurs")
        void shouldReturn400WhenErrorOccurs() throws Exception {
            // Given
            long initialOrderCount = orderRepository.count();

            OrderInputDTO orderInputDTO = new OrderInputDTO(
                    null,
                    List.of(new OrderItemInputDTO(null, "non-existent-id", 2))
            );

            // When & Then
            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderInputDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());

            // Verify that no order was saved
            assertThat(orderRepository.count()).isEqualTo(initialOrderCount);
        }
    }

    @Nested
    @DisplayName("DELETE /api/orders")
    class RemoveOrder {

        @Test
        @DisplayName("should remove an order")
        void shouldRemoveOrder() throws Exception {
            // Given
            OrderInputDTO orderInputDTO = new OrderInputDTO(
                    null,
                    List.of(new OrderItemInputDTO(null, testDish.getId(), 2))
            );
            MvcResult initialResult = mockMvc.perform(post("/api/customer-sessions")).andReturn();
            String sessionCookie = initialResult.getResponse().getHeader("Set-Cookie");
            Assertions.assertNotNull(sessionCookie);
            Cookie cookie = new Cookie("SESSION", sessionCookie.substring(8, sessionCookie.indexOf(";")));

            MvcResult orderInSessionResult = mockMvc.perform(patch("/api/customer-sessions")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderInputDTO))
            ).andReturn();

            String orderId = objectMapper.readTree(orderInSessionResult.getResponse().getContentAsString()).get("order").get("id").asText();

            // When
            MvcResult result = mockMvc.perform(delete("/api/orders")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.order").exists())
                    .andReturn();

            // Then
            // Extract the order ID from the response
            String responseBody = result.getResponse().getContentAsString();
            String returnedOrderId = objectMapper.readTree(responseBody).get("order").get("id").asText();
            // Verify that the returned order ID matches the one we provided
            assertThat(returnedOrderId).isEqualTo(orderId);

            // Verify that the order was removed from the repository
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            assertThat(orderOptional).isEmpty();
        }

        @Test
        @DisplayName("should return 400 when an error occurs")
        void shouldReturn400WhenErrorOccurs() throws Exception {
            // Given
            // No session cookie provided

            // When & Then
            mockMvc.perform(delete("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("should return 400 when trying to remove an immutable order")
        void shouldReturn400WhenTryingToRemoveImmutableOrder() throws Exception {
            // Given
            OrderInputDTO orderInputDTO = new OrderInputDTO(
                    null,
                    List.of(new OrderItemInputDTO(null, testDish.getId(), 2))
            );
            MvcResult initialResult = mockMvc.perform(post("/api/customer-sessions")).andReturn();
            String sessionCookie = initialResult.getResponse().getHeader("Set-Cookie");
            Assertions.assertNotNull(sessionCookie);
            Cookie cookie = new Cookie("SESSION", sessionCookie.substring(8, sessionCookie.indexOf(";")));

            MvcResult orderInSessionResult = mockMvc.perform(patch("/api/customer-sessions")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderInputDTO))
            ).andReturn();

            // Place the order to change its status to CHECKOUT
            mockMvc.perform(post("/api/orders")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());

            // Get the order ID
            String orderId = objectMapper.readTree(orderInSessionResult.getResponse().getContentAsString()).get("order").get("id").asText();

            // Manually update the order status to PAID (which is immutable)
            Order order = orderRepository.findById(orderId).orElseThrow();
            orderRepository.save(order.withStatus(OrderStatus.PAID));

            // When & Then
            mockMvc.perform(delete("/api/orders")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());

            // Verify that the order still exists
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            assertThat(orderOptional).isPresent();
            assertThat(orderOptional.get().getStatus()).isEqualTo(OrderStatus.PAID);
        }
    }
}
