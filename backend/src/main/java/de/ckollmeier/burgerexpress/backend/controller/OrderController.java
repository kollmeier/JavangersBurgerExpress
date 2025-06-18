package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.repository.OrderRepository;
import de.ckollmeier.burgerexpress.backend.service.CustomerSessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing orders.
 */
@RestController
@RequestMapping("/api/orders")
@PreAuthorize("permitAll()")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderRepository orderRepository;
    private final CustomerSessionService customerSessionService;

    /**
     * Places a new order with status PENDING.
     * @param session the HTTP session
     * @return the created order ID
     */
    @PostMapping
    public ResponseEntity<CustomerSessionDTO> placeOrder(HttpSession session) {
        Order order  = customerSessionService.getOrderFromCustomerSession(session)
                .orElseThrow(() -> new IllegalStateException("No customer session found"));

        // Save the order to the database
        Order savedOrder = orderRepository.save(order);

        log.info("Order placed with ID: {}", savedOrder.getId());

        // Return the order ID
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(customerSessionService.storeOrder(session, savedOrder)
                    .orElseThrow(() -> new IllegalStateException("No customer session found"))
            );
    }
}
