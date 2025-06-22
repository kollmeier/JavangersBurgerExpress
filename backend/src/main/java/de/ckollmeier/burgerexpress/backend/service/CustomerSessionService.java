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
import de.ckollmeier.burgerexpress.backend.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerSessionService {
    private static final long EXPIRATION_TIME_IN_SECONDS = 60L * 5; // 5 minutes
    public static final String SESSION_ATTRIBUTE_NAME = "customerSession";

    private final DishRepository dishRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;

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

    private Optional<CustomerSession> getRawCustomerSession(final HttpSession session) {
        CustomerSession customerSession = (CustomerSession) session.getAttribute(SESSION_ATTRIBUTE_NAME);

        if (customerSession != null && customerSession.expiresAt().isBefore(Instant.now())) {
            session.removeAttribute(SESSION_ATTRIBUTE_NAME);
            customerSession = null;
        }
        if (customerSession != null) {
            Order order = customerSession.order();
            if (order != null && order.getId() != null) {
                customerSession = customerSession.withOrder(
                    orderRepository.findById(order.getId()).orElse(order)
                );
            }
        }
        return Optional.ofNullable(customerSession);
    }

    public Optional<CustomerSessionDTO> getCustomerSession(final HttpSession session) {
        return getRawCustomerSession(session).map(
                CustomerSessionDTOConverter::convert
        );
    }

    public Optional<Order> getOrderFromCustomerSession(final HttpSession session) {
        return getRawCustomerSession(session).map(CustomerSession::order);
    }

    public Optional<CustomerSessionDTO> renewCustomerSession(final HttpSession session, long remainingSeconds) {
        Optional<CustomerSession> customerSession = getRawCustomerSession(session)
                .map(cs -> cs.withExpiresAt(Instant.now().plusSeconds(remainingSeconds)));

        if (customerSession.isPresent()) {
            session.setAttribute(SESSION_ATTRIBUTE_NAME, customerSession.orElse(null));

            return customerSession.map(CustomerSessionDTOConverter::convert);
        }
        return Optional.empty();
    }

    public Optional<CustomerSessionDTO> renewCustomerSession(final HttpSession session) {
        return renewCustomerSession(session, EXPIRATION_TIME_IN_SECONDS);
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

    public Optional<CustomerSessionDTO> storeOrder(final HttpSession session, final Order order) {
        Optional<CustomerSession> customerSession = getRawCustomerSession(session)
                .map(cs -> cs.withOrder(order).withExpiresAt(Instant.now().plusSeconds(EXPIRATION_TIME_IN_SECONDS)));

        session.setAttribute(SESSION_ATTRIBUTE_NAME, customerSession.orElse(null));

        return customerSession.map(CustomerSessionDTOConverter::convert);
    }

    public Optional<CustomerSessionDTO> storeOrder(final HttpSession session, final OrderInputDTO orderInputDTO) {
        return storeOrder(session, OrderConverter.convert(orderInputDTO, this::getOrderableItem));
    }
}
