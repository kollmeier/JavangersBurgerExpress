package de.ckollmeier.burgerexpress.backend.repository;


import de.ckollmeier.burgerexpress.backend.interfaces.Sortable;

import java.util.List;

public interface SortableRepository<T extends Sortable> {
    List<T> findAll(Class<T> theClass);
    List<T> saveAll(List<T> entity);
}
