package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.CustomerSessionDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO;
import de.ckollmeier.burgerexpress.backend.model.CustomerSession;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerSessionServiceTest {

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
                    false
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
                    Instant.parse("2023-01-01T12:05:00Z")
            );
            when(httpSession.getAttribute("customerSession")).thenReturn(existingSession);

            CustomerSessionDTO expectedDTO = new CustomerSessionDTO(
                    "2023-01-01 12:00:00",
                    "2023-01-01 12:05:00",
                    300L,
                    false
            );

            try (MockedStatic<CustomerSessionDTOConverter> converterMock = mockStatic(CustomerSessionDTOConverter.class)) {
                converterMock.when(() -> CustomerSessionDTOConverter.convert(existingSession))
                        .thenReturn(expectedDTO);

                // When
                CustomerSessionDTO result = customerSessionService.getCustomerSession(httpSession);

                // Then
                assertThat(result).isEqualTo(expectedDTO);
                verify(httpSession).getAttribute("customerSession");
                converterMock.verify(() -> CustomerSessionDTOConverter.convert(existingSession));
            }
        }

        @Test
        @DisplayName("Returns null when no CustomerSession exists in the session")
        void returnsNullWhenNoSessionExists() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);
            when(httpSession.getAttribute("customerSession")).thenReturn(null);

            // When
            CustomerSessionDTO result = customerSessionService.getCustomerSession(httpSession);

            // Then
            assertThat(result).isNull();
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
                    Instant.parse("2023-01-01T12:05:00Z")
            );
            when(httpSession.getAttribute("customerSession")).thenReturn(existingSession);

            CustomerSessionDTO expectedDTO = new CustomerSessionDTO(
                    "2023-01-01 12:00:00",
                    "2023-01-01 12:10:00",
                    600L,
                    false
            );

            try (MockedStatic<CustomerSessionDTOConverter> converterMock = mockStatic(CustomerSessionDTOConverter.class)) {
                converterMock.when(() -> CustomerSessionDTOConverter.convert(any(CustomerSession.class)))
                        .thenReturn(expectedDTO);

                // When
                CustomerSessionDTO result = customerSessionService.renewCustomerSession(httpSession);

                // Then
                assertThat(result).isEqualTo(expectedDTO);
                verify(httpSession).getAttribute("customerSession");
                verify(httpSession).setAttribute(eq("customerSession"), any(CustomerSession.class));
                converterMock.verify(() -> CustomerSessionDTOConverter.convert(any(CustomerSession.class)));
            }
        }

        @Test
        @DisplayName("Returns null when no CustomerSession exists in the session")
        void returnsNullWhenNoSessionExists() {
            // Given
            HttpSession httpSession = mock(HttpSession.class);
            when(httpSession.getAttribute("customerSession")).thenReturn(null);

            // When
            CustomerSessionDTO result = customerSessionService.renewCustomerSession(httpSession);

            // Then
            assertThat(result).isNull();
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
}
