package de.ckollmeier.burgerexpress.backend.repository;

import de.ckollmeier.burgerexpress.backend.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    /**
     * Find an order by its PayPal order ID
     * @param paypalOrderId The PayPal order ID
     * @return The order, or null if not found
     */
    Order findByPaypalOrderId(String paypalOrderId);
}
