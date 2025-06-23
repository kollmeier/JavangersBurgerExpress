package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.CustomerSessionDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderItemInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.CustomerSession;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.repository.MenuRepository;
import de.ckollmeier.burgerexpress.backend.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomerSessionServiceTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CustomerSessionService customerSessionService;

    @Nested
    @DisplayName("createCustomerSession(final HttpSession)")
    class CreateCustomerSession {

        @Test
        @DisplayName("Creates a new CustomerSession, stores it in the session, and returns a DTO")
        void createsNewSessionAndReturnsDTO() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);
            CustomerSessionDTO expectedDTO = new CustomerSessionDTO(
                    "2023-01-01 12:00:00",
                    "2023-01-01 12:05:00",
                    300L,
                    false,
                    new OrderOutputDTO(
                            "order-1",
                            0,
                            List.of(),
                            "0.00",
                            "2023-01-01 12:00:00",
                            "2023-01-01 12:00:00",
                            "NEW"
                    )
            );

            try (MockedStatic<CustomerSessionDTOConverter> converterMock = mockStatic(CustomerSessionDTOConverter.class)) {
                converterMock.when(() -> CustomerSessionDTOConverter.convert(any(CustomerSession.class)))
                        .thenReturn(expectedDTO);

                // When
                CustomerSessionDTO result = customerSessionService.createCustomerSession(httpSession);

                // Then
                assertThat(result).isEqualTo(expectedDTO);
                verify(httpSession).setAttribute(eq("customerSession"), any(CustomerSession.class));
                converterMock.verify(() -> CustomerSessionDTOConverter.convert(any(CustomerSession.class)));
            }
        }
    }

    @Nested
    @DisplayName("getCustomerSession(final HttpSession)")
    class GetCustomerSession {

        @Test
        @DisplayName("Returns existing CustomerSession from the session")
        void returnsExistingSession() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);
            CustomerSession existingSession = new CustomerSession(
                    Instant.parse("2023-01-01T12:00:00Z"),
                    Instant.now().plusSeconds(300), // Set expiration time to 5 minutes in the future
                    Order.builder().build()
            );
            when(httpSession.getAttribute("customerSession")).thenReturn(existingSession);

            CustomerSessionDTO expectedDTO = new CustomerSessionDTO(
                    "2023-01-01 12:00:00",
                    "2023-01-01 12:05:00",
                    300L,
                    false,
                    new OrderOutputDTO(
                            "order-1",
                            0,
                            List.of(),
                            "0.00",
                            "2023-01-01 12:00:00",
                            "2023-01-01 12:00:00",
                            "NEW"
                    )
            );

            try (MockedStatic<CustomerSessionDTOConverter> converterMock = mockStatic(CustomerSessionDTOConverter.class)) {
                converterMock.when(() -> CustomerSessionDTOConverter.convert(existingSession))
                        .thenReturn(expectedDTO);

                // When
                Optional<CustomerSessionDTO> result = customerSessionService.getCustomerSession(httpSession);

                // Then
                assertThat(result).isNotEmpty();
                assertThat(result).contains(expectedDTO);
                verify(httpSession).getAttribute("customerSession");
                converterMock.verify(() -> CustomerSessionDTOConverter.convert(existingSession));
            }
        }

        @Test
        @DisplayName("Returns Empty when no CustomerSession exists in the session")
        void returnsEmptyWhenNoSessionExists() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);
            when(httpSession.getAttribute("customerSession")).thenReturn(null);

            // When
            Optional<CustomerSessionDTO> result = customerSessionService.getCustomerSession(httpSession);

            // Then
            assertThat(result).isEmpty();
            verify(httpSession).getAttribute("customerSession");
            verifyNoMoreInteractions(httpSession);
        }
    }

    @Nested
    @DisplayName("renewCustomerSession(final HttpSession)")
    class RenewCustomerSession {

        @Test
        @DisplayName("Renews existing CustomerSession and returns updated DTO")
        void renewsExistingSession() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);
            CustomerSession existingSession = new CustomerSession(
                    Instant.parse("2023-01-01T12:00:00Z"),
                    Instant.now().plusSeconds(300), // Set expiration time to 5 minutes in the future
                    Order.builder().build()
            );
            when(httpSession.getAttribute("customerSession")).thenReturn(existingSession);

            CustomerSessionDTO expectedDTO = new CustomerSessionDTO(
                    "2023-01-01 12:00:00",
                    "2023-01-01 12:10:00",
                    600L,
                    false,
                    new OrderOutputDTO(
                            "order-1",
                            0,
                            List.of(),
                            "0.00",
                            "2023-01-01 12:00:00",
                            "2023-01-01 12:00:00",
                            "NEW"
                    )
            );

            try (MockedStatic<CustomerSessionDTOConverter> converterMock = mockStatic(CustomerSessionDTOConverter.class)) {
                converterMock.when(() -> CustomerSessionDTOConverter.convert(any(CustomerSession.class)))
                        .thenReturn(expectedDTO);

                // When
                Optional<CustomerSessionDTO> result = customerSessionService.renewCustomerSession(httpSession);

                // Then
                assertThat(result)
                        .isNotEmpty()
                        .contains(expectedDTO);
                verify(httpSession).getAttribute("customerSession");
                verify(httpSession).setAttribute(eq("customerSession"), any(CustomerSession.class));
                converterMock.verify(() -> CustomerSessionDTOConverter.convert(any(CustomerSession.class)));
            }
        }

        @Test
        @DisplayName("Returns Empty when no CustomerSession exists in the session")
        void returnsEmptyWhenNoSessionExists() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);
            when(httpSession.getAttribute("customerSession")).thenReturn(null);

            // When
            Optional<CustomerSessionDTO> result = customerSessionService.renewCustomerSession(httpSession);

            // Then
            assertThat(result).isEmpty();
            verify(httpSession).getAttribute("customerSession");
            verifyNoMoreInteractions(httpSession);
        }
    }

    @Nested
    @DisplayName("getOrderFromCustomerSession(final HttpSession)")
    class GetOrderFromCustomerSession {

        @Test
        @DisplayName("Returns order from existing CustomerSession")
        void returnsOrderFromExistingSession() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);
            Order expectedOrder = Order.builder().id("order-1").build();
            CustomerSession existingSession = new CustomerSession(
                    Instant.parse("2023-01-01T12:00:00Z"),
                    Instant.now().plusSeconds(300), // Set expiration time to 5 minutes in the future
                    expectedOrder
            );
            when(httpSession.getAttribute("customerSession")).thenReturn(existingSession);
            when(orderRepository.findById("order-1")).thenReturn(Optional.of(expectedOrder));

            // When
            Optional<Order> result = customerSessionService.getOrderFromCustomerSession(httpSession);

            // Then
            assertThat(result)
                    .isNotEmpty()
                    .contains(expectedOrder);
            verify(httpSession).getAttribute("customerSession");
            verify(orderRepository).findById("order-1");
        }

        @Test
        @DisplayName("Returns Empty when no CustomerSession exists in the session")
        void returnsEmptyWhenNoSessionExists() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);
            when(httpSession.getAttribute("customerSession")).thenReturn(null);

            // When
            Optional<Order> result = customerSessionService.getOrderFromCustomerSession(httpSession);

            // Then
            assertThat(result).isEmpty();
            verify(httpSession).getAttribute("customerSession");
            verifyNoMoreInteractions(httpSession);
        }
    }

    @Nested
    @DisplayName("removeCustomerSession(final HttpSession)")
    class RemoveCustomerSession {

        @Test
        @DisplayName("Removes CustomerSession from the session")
        void removesSessionFromHttpSession() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);

            // When
            customerSessionService.removeCustomerSession(httpSession);

            // Then
            verify(httpSession).removeAttribute("customerSession");
        }
    }

    @Nested
    @DisplayName("storeOrder(final HttpSession, final OrderInputDTO)")
    class StoreOrder {

        @Test
        @DisplayName("Returns Empty when no CustomerSession exists in the session")
        void returnsEmptyWhenNoSessionExists() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);
            OrderInputDTO orderInputDTO = new OrderInputDTO("order-1", List.of(
                    new OrderItemInputDTO(null, "item-1", 2)
            ));
            when(httpSession.getAttribute(CustomerSessionService.SESSION_ATTRIBUTE_NAME)).thenReturn(null);

            // Mock the repositories to handle the "item-1" ID
            Dish orderableItem = mock(Dish.class);
            when(orderableItem.getId()).thenReturn("item-1");
            when(orderableItem.getName()).thenReturn("Test Item");
            when(orderableItem.getPrice()).thenReturn(BigDecimal.valueOf(10.99));
            when(dishRepository.findById("item-1")).thenReturn(Optional.of(orderableItem));

            // When
            Optional<CustomerSessionDTO> result = customerSessionService.storeOrder(httpSession, orderInputDTO);

            // Then
            assertThat(result).isEmpty();
            verify(httpSession).getAttribute("customerSession");
            verify(httpSession).setAttribute(eq("customerSession"), isNull());
            verifyNoMoreInteractions(httpSession);
        }

        @Test
        @DisplayName("Stores order in CustomerSession and returns updated DTO")
        void storesOrderInCustomerSession() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);
            Order order = Order.builder()
                    .id("order-1")
                    .build();

            CustomerSession existingSession = new CustomerSession(
                    Instant.parse("2023-01-01T12:00:00Z"),
                    Instant.now().plusSeconds(300), // Set expiration time to 5 minutes in the future
                    Order.builder().build()
            );
            when(httpSession.getAttribute("customerSession")).thenReturn(existingSession);

            CustomerSessionDTO expectedDTO = new CustomerSessionDTO(
                    "2023-01-01 12:00:00",
                    "2023-01-01 12:05:00",
                    300L,
                    false,
                    new OrderOutputDTO(
                            "order-1",
                            0,
                            List.of(),
                            "0.00",
                            "2023-01-01 12:00:00",
                            "2023-01-01 12:00:00",
                            "NEW"
                    )
            );

            try (MockedStatic<CustomerSessionDTOConverter> customerSessionDTOConverterMock = mockStatic(CustomerSessionDTOConverter.class)) {
                customerSessionDTOConverterMock.when(() -> CustomerSessionDTOConverter.convert(any(CustomerSession.class)))
                        .thenReturn(expectedDTO);

                // When
                Optional<CustomerSessionDTO> result = customerSessionService.storeOrder(httpSession, order);

                // Then
                assertThat(result).isNotEmpty();
                assertThat(result).contains(expectedDTO);
                verify(httpSession).getAttribute("customerSession");
                verify(httpSession).setAttribute(eq("customerSession"), any(CustomerSession.class));
                customerSessionDTOConverterMock.verify(() -> CustomerSessionDTOConverter.convert(any(CustomerSession.class)));
            }
        }

        @Test
        @DisplayName("Tests getOrderableItem with DishRepository")
        void testGetOrderableItemWithDish() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);
            CustomerSession existingSession = new CustomerSession(
                    Instant.parse("2023-01-01T12:00:00Z"),
                    Instant.now().plusSeconds(300),
                    Order.builder().build()
            );
            when(httpSession.getAttribute("customerSession")).thenReturn(existingSession);

            // Mock Dish
            Dish dish = mock(Dish.class);
            when(dish.getId()).thenReturn("dish-1");
            when(dish.getName()).thenReturn("Test Dish");
            when(dish.getPrice()).thenReturn(BigDecimal.valueOf(10.99));
            when(dishRepository.findById("dish-1")).thenReturn(Optional.of(dish));

            // Create OrderInputDTO with the dish ID
            OrderInputDTO orderInputDTO = new OrderInputDTO("order-1", List.of(
                    new OrderItemInputDTO(null, "dish-1", 2)
            ));

            // Mock CustomerSessionDTOConverter
            CustomerSessionDTO expectedDTO = new CustomerSessionDTO(
                    "2023-01-01 12:00:00",
                    "2023-01-01 12:05:00",
                    300L,
                    false,
                    new OrderOutputDTO(
                            "order-1",
                            0,
                            List.of(),
                            "0.00",
                            "2023-01-01 12:00:00",
                            "2023-01-01 12:00:00",
                            "NEW"
                    )
            );

            try (MockedStatic<CustomerSessionDTOConverter> converterMock = mockStatic(CustomerSessionDTOConverter.class)) {
                converterMock.when(() -> CustomerSessionDTOConverter.convert(any(CustomerSession.class)))
                        .thenReturn(expectedDTO);

                // When - Call the method that will use getOrderableItem internally
                customerSessionService.storeOrder(httpSession, orderInputDTO);

                // Then - Verify that dishRepository.findById was called
                verify(dishRepository).findById("dish-1");
            }
        }

        @Test
        @DisplayName("Tests getOrderableItem with MenuRepository")
        void testGetOrderableItemWithMenu() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);
            CustomerSession existingSession = new CustomerSession(
                    Instant.parse("2023-01-01T12:00:00Z"),
                    Instant.now().plusSeconds(300),
                    Order.builder().build()
            );
            when(httpSession.getAttribute("customerSession")).thenReturn(existingSession);

            // Mock Dish not found
            when(dishRepository.findById("menu-1")).thenReturn(Optional.empty());

            // Mock Menu
            Menu menu = mock(Menu.class);
            when(menu.getId()).thenReturn("menu-1");
            when(menu.getName()).thenReturn("Test Menu");
            when(menu.getPrice()).thenReturn(BigDecimal.valueOf(15.99));
            when(menuRepository.findById("menu-1")).thenReturn(Optional.of(menu));

            // Create OrderInputDTO with the menu ID
            OrderInputDTO orderInputDTO = new OrderInputDTO("order-1", List.of(
                    new OrderItemInputDTO(null, "menu-1", 2)
            ));

            // Mock CustomerSessionDTOConverter
            CustomerSessionDTO expectedDTO = new CustomerSessionDTO(
                    "2023-01-01 12:00:00",
                    "2023-01-01 12:05:00",
                    300L,
                    false,
                    new OrderOutputDTO(
                            "order-1",
                            0,
                            List.of(),
                            "0.00",
                            "2023-01-01 12:00:00",
                            "2023-01-01 12:00:00",
                            "NEW"
                    )
            );

            try (MockedStatic<CustomerSessionDTOConverter> converterMock = mockStatic(CustomerSessionDTOConverter.class)) {
                converterMock.when(() -> CustomerSessionDTOConverter.convert(any(CustomerSession.class)))
                        .thenReturn(expectedDTO);

                // When - Call the method that will use getOrderableItem internally
                customerSessionService.storeOrder(httpSession, orderInputDTO);

                // Then - Verify that both repositories were called
                verify(dishRepository).findById("menu-1");
                verify(menuRepository).findById("menu-1");
            }
        }
    }
}
