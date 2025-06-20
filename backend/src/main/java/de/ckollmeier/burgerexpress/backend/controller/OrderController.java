package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.service.CustomerSessionService;
import de.ckollmeier.burgerexpress.backend.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
public class OrderController {
    private final OrderService orderService;
    private final CustomerSessionService customerSessionService;
    /**
     * Places a new order with status PENDING.
     * @param session the HTTP session
     * @return the session with created order
     */
    @PostMapping
    public ResponseEntity<CustomerSessionDTO> placeOrder(HttpSession session) {
        Order savedOrder = orderService.placeOrder(session);

        // Return the new Session
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(customerSessionService.storeOrder(session, savedOrder)
                    .orElseThrow(() -> new IllegalStateException("No customer session found"))
            );
    }

    @DeleteMapping
    public ResponseEntity<CustomerSessionDTO> removeOrder(HttpSession session) {
        Order removedOrder = orderService.removeOrder(session);

        // Return the updated Session
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerSessionService.storeOrder(session, removedOrder)
                        .orElseThrow(() -> new IllegalStateException("No customer session found"))
                );
    }
}
