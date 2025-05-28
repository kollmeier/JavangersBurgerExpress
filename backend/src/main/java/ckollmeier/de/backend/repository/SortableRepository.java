package ckollmeier.de.backend.repository;


import ckollmeier.de.backend.interfaces.Sortable;

import java.util.List;

public interface SortableRepository<T extends Sortable> {
    List<T> findAll(Class<T> theClass);
    List<T> saveAll(List<T> entity);
}
