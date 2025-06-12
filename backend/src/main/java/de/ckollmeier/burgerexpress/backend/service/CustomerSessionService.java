package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.CustomerSessionDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO;
import de.ckollmeier.burgerexpress.backend.model.CustomerSession;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CustomerSessionService {
    private static final long EXPIRATION_TIME_IN_SECONDS = 60L * 5; // 5 minutes
    private static final String SESSION_ATTRIBUTE_NAME = "customerSession";

    public CustomerSessionDTO createCustomerSession(final HttpSession session) {
        Instant now = Instant.now();
        CustomerSession customerSession = new CustomerSession(
                now,
                now.plusSeconds(EXPIRATION_TIME_IN_SECONDS)
        );
        session.setAttribute(SESSION_ATTRIBUTE_NAME, customerSession);
        return CustomerSessionDTOConverter.convert(customerSession);
    }

    public CustomerSessionDTO getCustomerSession(final HttpSession session) {
        CustomerSession customerSession = (CustomerSession) session.getAttribute(SESSION_ATTRIBUTE_NAME);
        if (customerSession == null) {
            return createCustomerSession(session);
        }
        return CustomerSessionDTOConverter.convert(customerSession);
    }

    public CustomerSessionDTO renewCustomerSession(final HttpSession session) {
        CustomerSession customerSession = (CustomerSession) session.getAttribute(SESSION_ATTRIBUTE_NAME);
        if (customerSession == null) {
            return null;
        }
        customerSession = customerSession.withExpiresAt(
                Instant.now().plusSeconds(EXPIRATION_TIME_IN_SECONDS)
        );
        session.setAttribute(SESSION_ATTRIBUTE_NAME, customerSession);
        return CustomerSessionDTOConverter.convert(customerSession);
    }

    public void removeCustomerSession(final HttpSession session) {
        session.removeAttribute(SESSION_ATTRIBUTE_NAME);
    }
}
