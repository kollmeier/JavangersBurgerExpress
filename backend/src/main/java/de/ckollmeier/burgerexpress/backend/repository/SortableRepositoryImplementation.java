package de.ckollmeier.burgerexpress.backend.repository;

import de.ckollmeier.burgerexpress.backend.interfaces.Sortable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SortableRepositoryImplementation<T extends Sortable> implements SortableRepository<T> {
    /**
     * The MongoTemplate instance used for database operations.
     *
     * @see MongoTemplate
     * @since 1.0
     */

    private final MongoTemplate mongoTemplate;

    /**
     * Saves all given entities.
     *
     * @param entities must not be {@literal null} nor must it contain {@literal null}.
     * @return the saved entities; will never be {@literal null}. The returned {@literal Iterable} will have the same size
     * as the {@literal Iterable} passed as an argument.
     */
    @Override
    public List<T> saveAll(final List<T> entities) {
        if (entities.isEmpty()) {
            return List.of();
        }
        List<T> returnedEntities = new ArrayList<>(entities.size());
        for (T entity : entities) {
            returnedEntities.add(mongoTemplate.save(entity));
        }
        return returnedEntities;
    }

    /**
     * Finds all entities of the specified class.
     *
     * @param theClass The class of the entities to find.
     * @return A list of entities matching the specified class.
     * @throws org.springframework.dao.DataAccessException in case of errors
     */
    @Override
    public List<T> findAll(final Class<T> theClass) {
        return mongoTemplate.find(new Query()
                .with(Sort
                        .by("position")
                        .ascending()
                        .and(Sort
                                .by("createdAt")
                                .descending())),
                theClass);
    }
}
