package de.ckollmeier.burgerexpress.backend.repository;

import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisplayCategoryRepository extends MongoRepository<DisplayCategory, String> {
    List<DisplayCategory> findAllByOrderByPositionAscCreatedAtDesc();
}
