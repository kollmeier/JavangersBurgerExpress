package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO;
import de.ckollmeier.burgerexpress.backend.model.CustomerSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerSessionDTOConverterTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @DisplayName("Convert CustomerSession to CustomerSessionDTO (not expired)")
    void convertCustomerSessionToDTO_NotExpired() {
        // Given
        Instant now = Instant.now();
        Instant expiresAt = now.plus(1, ChronoUnit.HOURS);
        CustomerSession customerSession = CustomerSession.builder()
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();

        // When
        CustomerSessionDTO dto = CustomerSessionDTOConverter.convert(customerSession);

        // Then
        assertThat(dto.createdAt()).isEqualTo(dateTimeFormatter.format(now.atZone(ZoneId.systemDefault())));
        assertThat(dto.expiresAt()).isEqualTo(dateTimeFormatter.format(expiresAt.atZone(ZoneId.systemDefault())));
        assertThat(dto.expiresInSeconds()).isPositive();
        assertThat(dto.expired()).isFalse();
    }

    @Test
    @DisplayName("Convert CustomerSession to CustomerSessionDTO (expired)")
    void convertCustomerSessionToDTO_Expired() {
        // Given
        Instant now = Instant.now();
        Instant createdAt = now.minus(2, ChronoUnit.HOURS);
        Instant expiresAt = now.minus(1, ChronoUnit.HOURS);
        CustomerSession customerSession = CustomerSession.builder()
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .build();

        // When
        CustomerSessionDTO dto = CustomerSessionDTOConverter.convert(customerSession);

        // Then
        assertThat(dto.createdAt()).isEqualTo(dateTimeFormatter.format(createdAt.atZone(ZoneId.systemDefault())));
        assertThat(dto.expiresAt()).isEqualTo(dateTimeFormatter.format(expiresAt.atZone(ZoneId.systemDefault())));
        assertThat(dto.expiresInSeconds()).isNegative();
        assertThat(dto.expired()).isTrue();
    }

    @Test
    @DisplayName("Constructor of utility class is private and throws exception")
    void constructor_is_private_and_throws() throws Exception {
        var ctor = CustomerSessionDTOConverter.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertThatThrownBy(ctor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("This is a utility class and cannot be instantiated");
    }
}
