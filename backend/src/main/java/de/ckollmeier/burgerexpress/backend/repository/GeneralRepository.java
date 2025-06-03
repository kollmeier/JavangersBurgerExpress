package de.ckollmeier.burgerexpress.backend.repository;

import de.ckollmeier.burgerexpress.backend.interfaces.FindableItem;

import java.util.Optional;

public interface GeneralRepository<T extends FindableItem> {
    Optional<T> findById(String id, Class<T> theClass);
}
