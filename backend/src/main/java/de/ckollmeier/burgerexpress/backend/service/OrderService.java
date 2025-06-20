package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.repository.OrderRepository;
import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final CustomerSessionService customerSessionService;
    private final OrderRepository orderRepository;

    public Order saveOrder(Order order) {
        Order savedOrder = orderRepository.save(order.withUpdatedAt(Instant.now()));
        log.info("Order saved with ID: {} and status {}", savedOrder.getId(), savedOrder.getStatus());
        return savedOrder;
    }

    public Order placeOrder(HttpSession session) {
        customerSessionService.renewCustomerSession(session);
        Order order  = customerSessionService.getOrderFromCustomerSession(session)
                .orElseThrow(() -> new IllegalStateException("No customer session found"));

        // Save the order to the database
        Order savedOrder = saveOrder(order.withStatus(
                order.getStatus() == OrderStatus.PENDING ? OrderStatus.CHECKOUT : order.getStatus()
        ));

        log.info("Order placed with ID: {} and status {}", savedOrder.getId(), savedOrder.getStatus());
        return savedOrder;
    }

    public Order removeOrder(HttpSession session) {
        customerSessionService.renewCustomerSession(session);
        Order order  = customerSessionService.getOrderFromCustomerSession(session)
                .orElseThrow(() -> new IllegalStateException("No customer session found"));

        if (order.getStatus().isImmutable()) {
            log.warn("Cannot remove order ID: {} with status {}", order.getId(), order.getStatus());
            throw new IllegalStateException("Cannot remove order with status " + order.getStatus());
        }

        orderRepository.delete(order);

        log.info("Order removed with ID: {}", order.getId());

        return order.withStatus(OrderStatus.PENDING);
    }
}
