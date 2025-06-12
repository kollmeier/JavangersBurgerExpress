package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderInputDTO;
import de.ckollmeier.burgerexpress.backend.service.CustomerSessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing customer sessions.
 */
@RestController
@RequestMapping("/api/customer-sessions")
@PreAuthorize("permitAll()")
@RequiredArgsConstructor
public class CustomerSessionController {

    /**
     * The service for customer sessions.
     */
    private final CustomerSessionService customerSessionService;

    /**
     * Gets the current customer session or creates a new one if none exists.
     * @param session the HTTP session
     * @return the customer session
     */
    @GetMapping
    public ResponseEntity<CustomerSessionDTO> getCustomerSession(final HttpSession session) {
        return new ResponseEntity<>(
                customerSessionService.getCustomerSession(session),
                HttpStatus.OK
        );
    }

    /**
     * Creates a new customer session.
     * @param session the HTTP session
     * @return the created customer session
     */
    @PostMapping
    public ResponseEntity<CustomerSessionDTO> createCustomerSession(final HttpSession session) {
        return new ResponseEntity<>(
                customerSessionService.createCustomerSession(session),
                HttpStatus.CREATED
        );
    }

    /**
     * Renews an existing customer session.
     * @param session the HTTP session
     * @return the renewed customer session or 404 if no session exists
     */
    @PutMapping
    public ResponseEntity<CustomerSessionDTO> renewCustomerSession(final HttpSession session) {
        CustomerSessionDTO renewedSession = customerSessionService.renewCustomerSession(session);
        if (renewedSession == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(renewedSession, HttpStatus.OK);
    }

    /**
     * Removes the current customer session.
     * @param session the HTTP session
     * @return no content
     */
    @DeleteMapping
    public ResponseEntity<Void> removeCustomerSession(final HttpSession session) {
        customerSessionService.removeCustomerSession(session);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    public ResponseEntity<CustomerSessionDTO> storeOrder(final HttpSession session, @RequestBody OrderInputDTO order) {
        CustomerSessionDTO updatedSession = customerSessionService.storeOrder(session, order);
        if (updatedSession == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedSession, HttpStatus.OK);
    }
}
