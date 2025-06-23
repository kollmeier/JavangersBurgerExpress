package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.converter.OrderOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.service.CustomerSessionService;
import de.ckollmeier.burgerexpress.backend.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing orders.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CustomerSessionService customerSessionService;
    /**
     * Places a new order with status CHECKOUT.
     * @param session the HTTP session
     * @return the session with created order
     */
    @PreAuthorize("permitAll()")
    @PostMapping
    public ResponseEntity<CustomerSessionDTO> placeOrder(HttpSession session) {
        Order savedOrder = orderService.placeOrder(session);

        // Return the new Session
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(customerSessionService.storeOrder(session, savedOrder)
                    .orElseThrow(() -> new IllegalStateException("No customer session found"))
            );
    }

    @PreAuthorize("permitAll()")
    @DeleteMapping
    public ResponseEntity<CustomerSessionDTO> removeOrder(HttpSession session) {
        Order removedOrder = orderService.removeOrder(session);

        // Return the updated Session
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerSessionService.storeOrder(session, removedOrder)
                        .orElseThrow(() -> new IllegalStateException("No customer session found"))
                );
    }

    @PreAuthorize("hasRole('KITCHEN')")
    @GetMapping("/kitchen")
    public List<OrderOutputDTO> getKitchenOrders() {
        return OrderOutputDTOConverter.convertFlattened(orderService.getTodaysOrdersForKitchen());
    }

    @PreAuthorize("hasRole('KITCHEN')")
    @PatchMapping("/kitchen/{orderId}")
    public OrderOutputDTO advanceKitchenOrder(@PathVariable String orderId) {
        return OrderOutputDTOConverter.convert(orderService.advanceKitchenOrder(orderId));
    }

    @PreAuthorize("hasRole('CASHIER')")
    @GetMapping("/cashier")
    public List<OrderOutputDTO> getCashierOrders() {
        return OrderOutputDTOConverter.convert(orderService.getTodaysOrdersForCashier());
    }

    @PreAuthorize("hasRole('CASHIER')")
    @PatchMapping("/cashier/{orderId}")
    public OrderOutputDTO advanceCashierOrder(@PathVariable String orderId) {
        return OrderOutputDTOConverter.convert(orderService.advanceCashierOrder(orderId));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/customer")
    public List<OrderOutputDTO> getCustomerOrders() {
        return OrderOutputDTOConverter.convert(orderService.getTodaysOrdersForCustomer());
    }
}
