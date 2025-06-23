package de.ckollmeier.burgerexpress.backend.repository;

import de.ckollmeier.burgerexpress.backend.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Order findByPaypalOrderId(String paypalOrderId);
    Optional<Order> findByStripePaymentOrderIdHash(String stripePaymentOrderIdHash);
    int getMaximumOrderNumberByUpdatedAtAfter(Instant updatedAt);
}
