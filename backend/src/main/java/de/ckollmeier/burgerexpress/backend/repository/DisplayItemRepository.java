package de.ckollmeier.burgerexpress.backend.repository;

import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisplayItemRepository extends MongoRepository<DisplayItem, String> {
    List<DisplayItem> findAllByOrderByPositionAsc();
}
