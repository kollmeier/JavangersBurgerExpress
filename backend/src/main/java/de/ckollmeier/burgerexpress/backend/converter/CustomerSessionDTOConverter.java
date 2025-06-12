package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO;
import de.ckollmeier.burgerexpress.backend.model.CustomerSession;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CustomerSessionDTOConverter {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class should not be instantiated.
     */
    private CustomerSessionDTOConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static CustomerSessionDTO convert(final CustomerSession customerSession) {
        return new CustomerSessionDTO(
                dateTimeFormatter.format(customerSession.createdAt().atZone(ZoneId.systemDefault())),
                dateTimeFormatter.format(customerSession.expiresAt().atZone(ZoneId.systemDefault())),
                Duration.between(Instant.now(), customerSession.expiresAt()).toSeconds(),
                Instant.now().isAfter(customerSession.expiresAt())
        );
    }


}
