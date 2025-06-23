package de.ckollmeier.burgerexpress.backend.repository;

import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Order findByPaypalOrderId(String paypalOrderId);
    Optional<Order> findByStripePaymentOrderIdHash(String stripePaymentOrderIdHash);
    List<Order> findAllByStatusIsInAndUpdatedAtAfter(Collection<OrderStatus> statuses, Instant updatedAt);
    Optional<Order> findTopByUpdatedAtAfterOrderByOrderNumberDesc(Instant updatedAt);
}
