package de.ckollmeier.burgerexpress.backend.repository;

import de.ckollmeier.burgerexpress.backend.interfaces.FindableItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GeneralRepositoryImplementation<T extends FindableItem> implements GeneralRepository<T> {
    /**
     * The MongoTemplate instance used for database operations.
     *
     * @see MongoTemplate
     * @since 1.0
     */

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<T> findById(String id, Class<T> theClass) {
        return Optional.ofNullable(mongoTemplate.findById(id, theClass));
    }
}
