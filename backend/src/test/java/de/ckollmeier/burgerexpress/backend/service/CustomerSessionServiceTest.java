package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.CustomerSessionDTOConverter;
import de.ckollmeier.burgerexpress.backend.converter.OrderConverter;
import de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderItemInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderOutputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.CustomerSession;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.repository.GeneralRepository;
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
    private GeneralRepository<OrderableItem> orderableItemRepository;

    @Mock
    private DishRepository dishRepository;

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
                assertThat(result.get()).isEqualTo(expectedDTO);
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
                assertThat(result).isNotEmpty();
                assertThat(result.get()).isEqualTo(expectedDTO);
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
            OrderInputDTO orderInputDTO = new OrderInputDTO("order-1", List.of(
                    new OrderItemInputDTO(null, "item-1", 2)
            ));

            CustomerSession existingSession = new CustomerSession(
                    Instant.parse("2023-01-01T12:00:00Z"),
                    Instant.now().plusSeconds(300), // Set expiration time to 5 minutes in the future
                    Order.builder().build()
            );
            when(httpSession.getAttribute("customerSession")).thenReturn(existingSession);

            // Mock OrderableItem
            OrderableItem orderableItem = mock(OrderableItem.class);
            when(orderableItem.getId()).thenReturn("item-1");
            when(orderableItem.getName()).thenReturn("Test Item");
            when(orderableItem.getPrice()).thenReturn(BigDecimal.valueOf(10.99));
            when(orderableItemRepository.findById("item-1", OrderableItem.class))
                .thenReturn(Optional.of(orderableItem));

            // Mock the converted Order
            Order convertedOrder = Order.builder()
                    .id("order-1")
                    .build();

            CustomerSessionDTO expectedDTO = new CustomerSessionDTO(
                    "2023-01-01 12:00:00",
                    "2023-01-01 12:05:00",
                    300L,
                    false,
                    new OrderOutputDTO(
                            "order-1",
                            List.of(),
                            "0.00",
                            "2023-01-01 12:00:00",
                            "2023-01-01 12:00:00",
                            "NEW"
                    )
            );

            try (MockedStatic<OrderConverter> orderConverterMock = mockStatic(OrderConverter.class);
                 MockedStatic<CustomerSessionDTOConverter> customerSessionDTOConverterMock = mockStatic(CustomerSessionDTOConverter.class)) {

                orderConverterMock.when(() -> OrderConverter.convert(eq(orderInputDTO), any()))
                        .thenReturn(convertedOrder);

                customerSessionDTOConverterMock.when(() -> CustomerSessionDTOConverter.convert(any(CustomerSession.class)))
                        .thenReturn(expectedDTO);

                // When
                Optional<CustomerSessionDTO> result = customerSessionService.storeOrder(httpSession, orderInputDTO);

                // Then
                assertThat(result).isNotEmpty();
                assertThat(result.get()).isEqualTo(expectedDTO);
                verify(httpSession).getAttribute("customerSession");
                verify(httpSession).setAttribute(eq("customerSession"), any(CustomerSession.class));
                orderConverterMock.verify(() -> OrderConverter.convert(eq(orderInputDTO), any()));
                customerSessionDTOConverterMock.verify(() -> CustomerSessionDTOConverter.convert(any(CustomerSession.class)));
            }
        }
    }
}
