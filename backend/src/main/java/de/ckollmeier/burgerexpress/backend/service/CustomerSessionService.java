package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.CustomerSessionDTOConverter;
import de.ckollmeier.burgerexpress.backend.converter.OrderConverter;
import de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderInputDTO;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.CustomerSession;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.repository.MenuRepository;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CustomerSessionService {
    private static final long EXPIRATION_TIME_IN_SECONDS = 60L * 5; // 5 minutes
    private static final String SESSION_ATTRIBUTE_NAME = "customerSession";

    private final DishRepository dishRepository;
    private final MenuRepository menuRepository;

    public CustomerSessionDTO createCustomerSession(final HttpSession session) {
        Instant now = Instant.now();
        CustomerSession customerSession = new CustomerSession(
                now,
                now.plusSeconds(EXPIRATION_TIME_IN_SECONDS),
                Order.builder().build()
        );
        session.setAttribute(SESSION_ATTRIBUTE_NAME, customerSession);
        return CustomerSessionDTOConverter.convert(customerSession);
    }

    public CustomerSessionDTO getCustomerSession(final HttpSession session) {
        CustomerSession customerSession = (CustomerSession) session.getAttribute(SESSION_ATTRIBUTE_NAME);
        if (customerSession == null) {
            return null;
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

    private OrderableItem getOrderableItem(final @NonNull String id) {
        Dish dish = dishRepository.findById(id).orElse(null);
        if (dish != null) {
            return dish;
        }

        return menuRepository.findById(id).orElseThrow(() -> new NotFoundException("Item for Order not found"));
    }

    public CustomerSessionDTO storeOrder(final HttpSession session, final OrderInputDTO orderInputDTO) {
        CustomerSession customerSession = (CustomerSession) session.getAttribute(SESSION_ATTRIBUTE_NAME);
        if (customerSession == null) {
            return null;
        }
        customerSession = customerSession.withOrder(OrderConverter.convert(orderInputDTO, this::getOrderableItem));
        session.setAttribute(SESSION_ATTRIBUTE_NAME, customerSession);

        return CustomerSessionDTOConverter.convert(customerSession);
    }
}
