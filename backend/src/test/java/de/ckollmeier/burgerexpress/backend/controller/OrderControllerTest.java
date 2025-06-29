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
import java.time.Instant;

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
            ).andExpect(status().isOk())
              .andReturn();

            String orderId = objectMapper.readTree(orderInSessionResult.getResponse().getContentAsString()).get("order").get("id").asText();

            // When
            System.out.println("[DEBUG_LOG] testDish ID: " + testDish.getId());
            System.out.println("[DEBUG_LOG] Order ID: " + orderId);
            MvcResult result = mockMvc.perform(post("/api/orders")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(mvcResult -> System.out.println("[DEBUG_LOG] Response: " + mvcResult.getResponse().getContentAsString()))
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
        @DisplayName("should include orderNumber in the response when placing an order")
        void shouldIncludeOrderNumberInResponse() throws Exception {
            // Given
            OrderInputDTO orderInputDTO = new OrderInputDTO(
                    null,
                    List.of(new OrderItemInputDTO(null, testDish.getId(), 2))
            );
            MvcResult initialResult = mockMvc.perform(post("/api/customer-sessions")).andReturn();
            String sessionCookie = initialResult.getResponse().getHeader("Set-Cookie");
            Assertions.assertNotNull(sessionCookie);
            Cookie cookie = new Cookie("SESSION", sessionCookie.substring(8, sessionCookie.indexOf(";")));

            mockMvc.perform(patch("/api/customer-sessions")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderInputDTO))
            ).andExpect(status().isOk())
              .andReturn();

            // When
            MvcResult result = mockMvc.perform(post("/api/orders")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.order").exists())
                    .andReturn();

            // Then
            String responseBody = result.getResponse().getContentAsString();
            int orderNumber = objectMapper.readTree(responseBody).get("order").get("orderNumber").asInt();

            // Verify that the orderNumber is at least 101 (minimum value)
            assertThat(orderNumber).isGreaterThanOrEqualTo(101);

            // Verify that the orderNumber is also set in the database
            String orderId = objectMapper.readTree(responseBody).get("order").get("id").asText();
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            assertThat(orderOptional).isPresent();
            assertThat(orderOptional.get().getOrderNumber()).isEqualTo(orderNumber);
        }

        @Test
        @DisplayName("should have valid orderNumber for multiple orders placed in the same day")
        void shouldHaveValidOrderNumberForMultipleOrders() throws Exception {
            // Given - Place first order
            OrderInputDTO orderInputDTO = new OrderInputDTO(
                    null,
                    List.of(new OrderItemInputDTO(null, testDish.getId(), 2))
            );

            // Create first session and place first order
            MvcResult initialResult1 = mockMvc.perform(post("/api/customer-sessions")).andReturn();
            String sessionCookie1 = initialResult1.getResponse().getHeader("Set-Cookie");
            Assertions.assertNotNull(sessionCookie1);
            Cookie cookie1 = new Cookie("SESSION", sessionCookie1.substring(8, sessionCookie1.indexOf(";")));

            mockMvc.perform(patch("/api/customer-sessions")
                    .cookie(cookie1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderInputDTO))
            ).andExpect(status().isOk())
              .andReturn();

            MvcResult result1 = mockMvc.perform(post("/api/orders")
                            .cookie(cookie1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

            // Get first order number
            String responseBody1 = result1.getResponse().getContentAsString();
            int orderNumber1 = objectMapper.readTree(responseBody1).get("order").get("orderNumber").asInt();

            // Verify that the first order number is at least 101
            assertThat(orderNumber1).isGreaterThanOrEqualTo(101);

            // Create second session and place second order
            MvcResult initialResult2 = mockMvc.perform(post("/api/customer-sessions")).andReturn();
            String sessionCookie2 = initialResult2.getResponse().getHeader("Set-Cookie");
            Assertions.assertNotNull(sessionCookie2);
            Cookie cookie2 = new Cookie("SESSION", sessionCookie2.substring(8, sessionCookie2.indexOf(";")));

            mockMvc.perform(patch("/api/customer-sessions")
                    .cookie(cookie2)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderInputDTO))
            ).andExpect(status().isOk())
              .andReturn();

            // When - Place second order
            MvcResult result2 = mockMvc.perform(post("/api/orders")
                            .cookie(cookie2)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

            // Then - Get second order number and verify it's valid
            String responseBody2 = result2.getResponse().getContentAsString();
            int orderNumber2 = objectMapper.readTree(responseBody2).get("order").get("orderNumber").asInt();

            // Verify that the second order number is at least 102
            assertThat(orderNumber2).isGreaterThanOrEqualTo(orderNumber1 + 1);
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
    @DisplayName("GET /api/orders/kitchen")
    class GetKitchenOrders {

        @Test
        @DisplayName("should return kitchen orders with KITCHEN role")
        @WithMockUser(roles = {"KITCHEN"})
        void shouldReturnKitchenOrdersWithKitchenRole() throws Exception {
            // Given
            // Create and save orders with different statuses
            Instant now = Instant.now();
            Order paidOrder = Order.builder()
                    .status(OrderStatus.PAID)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order inProgressOrder = Order.builder()
                    .status(OrderStatus.IN_PROGRESS)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order readyOrder = Order.builder()
                    .status(OrderStatus.READY)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order deliveredOrder = Order.builder()
                    .status(OrderStatus.DELIVERED)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            orderRepository.saveAll(List.of(paidOrder, inProgressOrder, readyOrder, deliveredOrder));

            // When & Then
            mockMvc.perform(get("/api/orders/kitchen"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[?(@.status=='PAID')]").exists())
                    .andExpect(jsonPath("$[?(@.status=='IN_PROGRESS')]").exists())
                    .andExpect(jsonPath("$[?(@.status=='DELIVERED')]").doesNotExist());
        }

        @Test
        @DisplayName("should not return yesterday's orders")
        @WithMockUser(roles = {"KITCHEN"})
        void shouldNotReturnYesterdaysOrders() throws Exception {
            // Given
            // Create and save orders with yesterday's date
            Instant yesterday = Instant.now().minus(1, java.time.temporal.ChronoUnit.DAYS).minus(1, java.time.temporal.ChronoUnit.HOURS);
            Instant today = Instant.now();

            // Yesterday's orders with kitchen statuses
            Order yesterdayPaidOrder = Order.builder()
                    .status(OrderStatus.PAID)
                    .createdAt(yesterday)
                    .updatedAt(yesterday)
                    .build();
            Order yesterdayInProgressOrder = Order.builder()
                    .status(OrderStatus.IN_PROGRESS)
                    .createdAt(yesterday)
                    .updatedAt(yesterday)
                    .build();

            // Today's orders with kitchen statuses
            Order todayPaidOrder = Order.builder()
                    .status(OrderStatus.PAID)
                    .createdAt(today)
                    .updatedAt(today)
                    .build();
            Order todayInProgressOrder = Order.builder()
                    .status(OrderStatus.IN_PROGRESS)
                    .createdAt(today)
                    .updatedAt(today)
                    .build();

            // Save orders and get the saved instances with IDs
            List<Order> savedOrders = orderRepository.saveAll(List.of(
                    yesterdayPaidOrder, 
                    yesterdayInProgressOrder, 
                    todayPaidOrder, 
                    todayInProgressOrder
            ));

            // Extract the saved orders with their IDs
            Order savedYesterdayPaidOrder = savedOrders.get(0);
            Order savedYesterdayInProgressOrder = savedOrders.get(1);
            Order savedTodayPaidOrder = savedOrders.get(2);
            Order savedTodayInProgressOrder = savedOrders.get(3);

            // When & Then
            mockMvc.perform(get("/api/orders/kitchen"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    // Verify that today's orders are returned
                    .andExpect(jsonPath("$[?(@.id=='" + savedTodayPaidOrder.getId() + "')]").exists())
                    .andExpect(jsonPath("$[?(@.id=='" + savedTodayInProgressOrder.getId() + "')]").exists())
                    // Verify that yesterday's orders are not returned
                    .andExpect(jsonPath("$[?(@.id=='" + savedYesterdayPaidOrder.getId() + "')]").doesNotExist())
                    .andExpect(jsonPath("$[?(@.id=='" + savedYesterdayInProgressOrder.getId() + "')]").doesNotExist());
        }

        @Test
        @DisplayName("should not return kitchen orders without KITCHEN role")
        @WithMockUser(roles = {"MANAGER"})
        void shouldNotReturnKitchenOrdersWithoutKitchenRole() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/orders/kitchen"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("should not return kitchen orders for anonymous users")
        @WithMockUser(roles = {})
        void shouldNotReturnKitchenOrdersForAnonymousUsers() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/orders/kitchen"))
                    .andExpect(status().isForbidden());
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
            ).andExpect(status().isOk())
              .andReturn();

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

    @Nested
    @DisplayName("GET /api/orders/cashier")
    class GetCashierOrders {

        @Test
        @DisplayName("should return cashier orders with CASHIER role")
        @WithMockUser(roles = {"CASHIER"})
        void shouldReturnCashierOrdersWithCashierRole() throws Exception {
            // Given
            // Create and save orders with different statuses
            Instant now = Instant.now();
            Order readyOrder = Order.builder()
                    .status(OrderStatus.READY)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order deliveredOrder = Order.builder()
                    .status(OrderStatus.DELIVERED)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order inProgressOrder = Order.builder()
                    .status(OrderStatus.IN_PROGRESS)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            orderRepository.saveAll(List.of(readyOrder, deliveredOrder, inProgressOrder));

            // When & Then
            mockMvc.perform(get("/api/orders/cashier"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[?(@.status=='READY')]").exists())
                    .andExpect(jsonPath("$[?(@.status=='DELIVERED')]").exists())
                    .andExpect(jsonPath("$[?(@.status=='IN_PROGRESS')]").doesNotExist());
        }

        @Test
        @DisplayName("should not return yesterday's orders")
        @WithMockUser(roles = {"CASHIER"})
        void shouldNotReturnYesterdaysOrders() throws Exception {
            // Given
            // Create and save orders with yesterday's date
            Instant yesterday = Instant.now().minus(1, java.time.temporal.ChronoUnit.DAYS).minus(1, java.time.temporal.ChronoUnit.HOURS);
            Instant today = Instant.now();

            // Yesterday's orders with cashier statuses
            Order yesterdayReadyOrder = Order.builder()
                    .status(OrderStatus.READY)
                    .createdAt(yesterday)
                    .updatedAt(yesterday)
                    .build();
            Order yesterdayDeliveredOrder = Order.builder()
                    .status(OrderStatus.DELIVERED)
                    .createdAt(yesterday)
                    .updatedAt(yesterday)
                    .build();

            // Today's orders with cashier statuses
            Order todayReadyOrder = Order.builder()
                    .status(OrderStatus.READY)
                    .createdAt(today)
                    .updatedAt(today)
                    .build();
            Order todayDeliveredOrder = Order.builder()
                    .status(OrderStatus.DELIVERED)
                    .createdAt(today)
                    .updatedAt(today)
                    .build();

            // Save orders and get the saved instances with IDs
            List<Order> savedOrders = orderRepository.saveAll(List.of(
                    yesterdayReadyOrder, 
                    yesterdayDeliveredOrder, 
                    todayReadyOrder, 
                    todayDeliveredOrder
            ));

            // Extract the saved orders with their IDs
            Order savedYesterdayReadyOrder = savedOrders.get(0);
            Order savedYesterdayDeliveredOrder = savedOrders.get(1);
            Order savedTodayReadyOrder = savedOrders.get(2);
            Order savedTodayDeliveredOrder = savedOrders.get(3);

            // When & Then
            mockMvc.perform(get("/api/orders/cashier"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    // Verify that today's orders are returned
                    .andExpect(jsonPath("$[?(@.id=='" + savedTodayReadyOrder.getId() + "')]").exists())
                    .andExpect(jsonPath("$[?(@.id=='" + savedTodayDeliveredOrder.getId() + "')]").exists())
                    // Verify that yesterday's orders are not returned
                    .andExpect(jsonPath("$[?(@.id=='" + savedYesterdayReadyOrder.getId() + "')]").doesNotExist())
                    .andExpect(jsonPath("$[?(@.id=='" + savedYesterdayDeliveredOrder.getId() + "')]").doesNotExist());
        }

        @Test
        @DisplayName("should not return cashier orders without CASHIER role")
        @WithMockUser(roles = {"MANAGER"})
        void shouldNotReturnCashierOrdersWithoutCashierRole() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/orders/cashier"))
                    .andExpect(status().isForbidden());
        }

    }

    @Nested
    @DisplayName("PATCH /api/orders/cashier/{orderId}")
    class AdvanceCashierOrder {

        @Test
        @DisplayName("should advance a READY order to DELIVERED with CASHIER role")
        @WithMockUser(roles = {"CASHIER"})
        void shouldAdvanceReadyOrderToDelivered() throws Exception {
            // Given
            // Create and save a READY order
            Instant now = Instant.now();
            Order readyOrder = Order.builder()
                    .status(OrderStatus.READY)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order savedOrder = orderRepository.save(readyOrder);

            // When
            mockMvc.perform(patch("/api/orders/cashier/" + savedOrder.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("DELIVERED"))
                    .andReturn();

            // Then
            // Verify that the order status was updated in the database
            Optional<Order> updatedOrderOptional = orderRepository.findById(savedOrder.getId());
            assertThat(updatedOrderOptional).isPresent();
            assertThat(updatedOrderOptional.get().getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        @DisplayName("should return 400 when trying to advance a non-cashier order")
        @WithMockUser(roles = {"CASHIER"})
        void shouldReturn400WhenTryingToAdvanceNonCashierOrder() throws Exception {
            // Given
            // Create and save an IN_PROGRESS order (not a cashier status)
            Instant now = Instant.now();
            Order inProgressOrder = Order.builder()
                    .status(OrderStatus.IN_PROGRESS)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order savedOrder = orderRepository.save(inProgressOrder);

            // When & Then
            mockMvc.perform(patch("/api/orders/cashier/" + savedOrder.getId()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());

            // Verify that the order status was not changed
            Optional<Order> orderOptional = orderRepository.findById(savedOrder.getId());
            assertThat(orderOptional).isPresent();
            assertThat(orderOptional.get().getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("should return 404 when order is not found")
        @WithMockUser(roles = {"CASHIER"})
        void shouldReturn404WhenOrderNotFound() throws Exception {
            // When & Then
            mockMvc.perform(patch("/api/orders/cashier/non-existent-id"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should not advance cashier order without CASHIER role")
        @WithMockUser(roles = {"MANAGER"})
        void shouldNotAdvanceCashierOrderWithoutCashierRole() throws Exception {
            // Given
            // Create and save a READY order
            Instant now = Instant.now();
            Order readyOrder = Order.builder()
                    .status(OrderStatus.READY)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order savedOrder = orderRepository.save(readyOrder);

            // When & Then
            mockMvc.perform(patch("/api/orders/cashier/" + savedOrder.getId()))
                    .andExpect(status().isForbidden());

            // Verify that the order status was not changed
            Optional<Order> orderOptional = orderRepository.findById(savedOrder.getId());
            assertThat(orderOptional).isPresent();
            assertThat(orderOptional.get().getStatus()).isEqualTo(OrderStatus.READY);
        }
    }

    @Nested
    @DisplayName("GET /api/orders/customer")
    class GetCustomerOrders {

        @Test
        @DisplayName("should return customer orders for all users")
        void shouldReturnCustomerOrdersForAllUsers() throws Exception {
            // Given
            // Create and save orders with different statuses
            Instant now = Instant.now();
            Order inProgressOrder = Order.builder()
                    .status(OrderStatus.IN_PROGRESS)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order readyOrder = Order.builder()
                    .status(OrderStatus.READY)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order deliveredOrder = Order.builder()
                    .status(OrderStatus.DELIVERED)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            orderRepository.saveAll(List.of(inProgressOrder, readyOrder, deliveredOrder));

            // When & Then
            mockMvc.perform(get("/api/orders/customer"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[?(@.status=='IN_PROGRESS')]").exists())
                    .andExpect(jsonPath("$[?(@.status=='READY')]").exists())
                    .andExpect(jsonPath("$[?(@.status=='DELIVERED')]").doesNotExist());
        }

        @Test
        @DisplayName("should not return yesterday's orders")
        void shouldNotReturnYesterdaysOrders() throws Exception {
            // Given
            // Create and save orders with yesterday's date
            Instant yesterday = Instant.now().minus(1, java.time.temporal.ChronoUnit.DAYS).minus(1, java.time.temporal.ChronoUnit.HOURS);
            Instant today = Instant.now();

            // Yesterday's orders with customer statuses
            Order yesterdayInProgressOrder = Order.builder()
                    .status(OrderStatus.IN_PROGRESS)
                    .createdAt(yesterday)
                    .updatedAt(yesterday)
                    .build();
            Order yesterdayReadyOrder = Order.builder()
                    .status(OrderStatus.READY)
                    .createdAt(yesterday)
                    .updatedAt(yesterday)
                    .build();

            // Today's orders with customer statuses
            Order todayInProgressOrder = Order.builder()
                    .status(OrderStatus.IN_PROGRESS)
                    .createdAt(today)
                    .updatedAt(today)
                    .build();
            Order todayReadyOrder = Order.builder()
                    .status(OrderStatus.READY)
                    .createdAt(today)
                    .updatedAt(today)
                    .build();

            // Save orders and get the saved instances with IDs
            List<Order> savedOrders = orderRepository.saveAll(List.of(
                    yesterdayInProgressOrder, 
                    yesterdayReadyOrder, 
                    todayInProgressOrder, 
                    todayReadyOrder
            ));

            // Extract the saved orders with their IDs
            Order savedYesterdayInProgressOrder = savedOrders.get(0);
            Order savedYesterdayReadyOrder = savedOrders.get(1);
            Order savedTodayInProgressOrder = savedOrders.get(2);
            Order savedTodayReadyOrder = savedOrders.get(3);

            // When & Then
            mockMvc.perform(get("/api/orders/customer"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    // Verify that today's orders are returned
                    .andExpect(jsonPath("$[?(@.id=='" + savedTodayInProgressOrder.getId() + "')]").exists())
                    .andExpect(jsonPath("$[?(@.id=='" + savedTodayReadyOrder.getId() + "')]").exists())
                    // Verify that yesterday's orders are not returned
                    .andExpect(jsonPath("$[?(@.id=='" + savedYesterdayInProgressOrder.getId() + "')]").doesNotExist())
                    .andExpect(jsonPath("$[?(@.id=='" + savedYesterdayReadyOrder.getId() + "')]").doesNotExist());
        }
    }

    @Nested
    @DisplayName("PATCH /api/orders/kitchen/{orderId}")
    class AdvanceKitchenOrder {

        @Test
        @DisplayName("should advance a PAID order to IN_PROGRESS with KITCHEN role")
        @WithMockUser(roles = {"KITCHEN"})
        void shouldAdvancePaidOrderToInProgress() throws Exception {
            // Given
            // Create and save a PAID order
            Instant now = Instant.now();
            Order paidOrder = Order.builder()
                    .status(OrderStatus.PAID)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order savedOrder = orderRepository.save(paidOrder);

            // When
            mockMvc.perform(patch("/api/orders/kitchen/" + savedOrder.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                    .andReturn();

            // Then
            // Verify that the order status was updated in the database
            Optional<Order> updatedOrderOptional = orderRepository.findById(savedOrder.getId());
            assertThat(updatedOrderOptional).isPresent();
            assertThat(updatedOrderOptional.get().getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("should advance an IN_PROGRESS order to READY with KITCHEN role")
        @WithMockUser(roles = {"KITCHEN"})
        void shouldAdvanceInProgressOrderToReady() throws Exception {
            // Given
            // Create and save an IN_PROGRESS order
            Instant now = Instant.now();
            Order inProgressOrder = Order.builder()
                    .status(OrderStatus.IN_PROGRESS)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order savedOrder = orderRepository.save(inProgressOrder);

            // When
            mockMvc.perform(patch("/api/orders/kitchen/" + savedOrder.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("READY"))
                    .andReturn();

            // Then
            // Verify that the order status was updated in the database
            Optional<Order> updatedOrderOptional = orderRepository.findById(savedOrder.getId());
            assertThat(updatedOrderOptional).isPresent();
            assertThat(updatedOrderOptional.get().getStatus()).isEqualTo(OrderStatus.READY);
        }

        @Test
        @DisplayName("should return 400 when trying to advance a non-kitchen order")
        @WithMockUser(roles = {"KITCHEN"})
        void shouldReturn400WhenTryingToAdvanceNonKitchenOrder() throws Exception {
            // Given
            // Create and save a CHECKOUT order (not a kitchen status)
            Instant now = Instant.now();
            Order checkoutOrder = Order.builder()
                    .status(OrderStatus.CHECKOUT)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order savedOrder = orderRepository.save(checkoutOrder);

            // When & Then
            mockMvc.perform(patch("/api/orders/kitchen/" + savedOrder.getId()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());

            // Verify that the order status was not changed
            Optional<Order> orderOptional = orderRepository.findById(savedOrder.getId());
            assertThat(orderOptional).isPresent();
            assertThat(orderOptional.get().getStatus()).isEqualTo(OrderStatus.CHECKOUT);
        }

        @Test
        @DisplayName("should return 404 when order is not found")
        @WithMockUser(roles = {"KITCHEN"})
        void shouldReturn404WhenOrderNotFound() throws Exception {
            // When & Then
            mockMvc.perform(patch("/api/orders/kitchen/non-existent-id"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should not advance kitchen order without KITCHEN role")
        @WithMockUser(roles = {"MANAGER"})
        void shouldNotAdvanceKitchenOrderWithoutKitchenRole() throws Exception {
            // Given
            // Create and save a PAID order
            Instant now = Instant.now();
            Order paidOrder = Order.builder()
                    .status(OrderStatus.PAID)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Order savedOrder = orderRepository.save(paidOrder);

            // When & Then
            mockMvc.perform(patch("/api/orders/kitchen/" + savedOrder.getId()))
                    .andExpect(status().isForbidden());

            // Verify that the order status was not changed
            Optional<Order> orderOptional = orderRepository.findById(savedOrder.getId());
            assertThat(orderOptional).isPresent();
            assertThat(orderOptional.get().getStatus()).isEqualTo(OrderStatus.PAID);
        }
    }
}
