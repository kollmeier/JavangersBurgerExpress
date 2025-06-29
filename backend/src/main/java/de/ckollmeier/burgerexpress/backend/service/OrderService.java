package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.repository.OrderRepository;
import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final CustomerSessionService customerSessionService;
    private final OrderRepository orderRepository;

    public Order saveOrder(Order order) {
        Order savedOrder = orderRepository.save(order.withUpdatedAt(Instant.now()));
        log.info("Order {} saved with ID: {} and status {}", savedOrder.getOrderNumber(), savedOrder.getId(), savedOrder.getStatus());
        return savedOrder;
    }

    public Order placeOrder(HttpSession session) {
        customerSessionService.renewCustomerSession(session);
        Order order  = customerSessionService.getOrderFromCustomerSession(session)
                .orElseThrow(() -> new IllegalStateException("No customer session found"));

        // Save the order to the database with status CHECKOUT
        int maxOrderNumber = orderRepository.findTopByUpdatedAtAfterOrderByOrderNumberDesc(Instant.now().minus(1, ChronoUnit.DAYS))
                .map(Order::getOrderNumber)
                .orElse(0);

        Order savedOrder = saveOrder(
                order
                .withStatus(OrderStatus.CHECKOUT)
                .withUpdatedAt(Instant.now())
                        .withOrderNumber(Math.max(101, maxOrderNumber + 1))
        );

        log.info("Order {} placed with ID: {} and status {}", savedOrder.getOrderNumber(), savedOrder.getId(), savedOrder.getStatus());
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

        log.info("Order {} removed with ID: {}", order.getOrderNumber(), order.getId());

        return order.withStatus(OrderStatus.PENDING);
    }

    public List<Order> getTodaysOrdersForKitchen() {
        return orderRepository.findAllByStatusIsInAndUpdatedAtAfter(OrderStatus.getKitchenStatuses(), Instant.now().minus(1, ChronoUnit.DAYS));
    }

    public Order advanceKitchenOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getStatus().isKitchen()) {
            log.warn("Cannot advance order ID: {} with status {}", order.getId(), order.getStatus());
            throw new IllegalStateException("Cannot advance order with status " + order.getStatus());
        }

        return orderRepository.save(order.withStatus(order.getStatus().advancedStatus()));
    }

    public List<Order> getTodaysOrdersForCashier() {
        return orderRepository.findAllByStatusIsInAndUpdatedAtAfter(OrderStatus.getCashierStatuses(), Instant.now().minus(1, ChronoUnit.DAYS));
    }

    public Order advanceCashierOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getStatus().isCashier()) {
            log.warn("Cannot advance order ID: {} with status {}", order.getId(), order.getStatus());
            throw new IllegalStateException("Cannot advance order with status " + order.getStatus());
        }

        return orderRepository.save(order.withStatus(order.getStatus().advancedStatus()));
    }

    public List<Order> getTodaysOrdersForCustomer() {
        return orderRepository.findAllByStatusIsInAndUpdatedAtAfter(OrderStatus.getCustomerStatuses(), Instant.now().minus(1, ChronoUnit.DAYS));
    }
}
